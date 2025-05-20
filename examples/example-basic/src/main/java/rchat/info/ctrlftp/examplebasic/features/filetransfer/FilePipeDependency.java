package rchat.info.ctrlftp.examplebasic.features.filetransfer;

import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.filetransfer.BasePipeDependency;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class FilePipeDependency extends BasePipeDependency<FilePipeRecord> {
    public enum TransferTypes {
        ASCII("A"),
        EBCDIC("E"),
        IMAGE("I");

        public final String val;

        TransferTypes(String val) {
            this.val = val;
        }
    }

    private TransferTypes transferType = TransferTypes.ASCII;

    public Response setType(String type) {
        var parsedTransferType = Arrays.stream(TransferTypes.values())
                .filter(tranferType -> tranferType.val.equals(type))
                .findFirst();

        if (parsedTransferType.isEmpty()) {
            return new Response(ResponseTypes.BAD_PARAMETERS);
        }

        this.transferType = parsedTransferType.get();
        return new Response(ResponseTypes.COMMAND_OK, "Set up transfer type");
    }

    @Override
    public FilePipeRecord pipeClientInputToDataClass(InputStream client) {
        if (transferType == TransferTypes.IMAGE) {
            try {
                File tempFile = File.createTempFile(this.getClass().getName() + "-", ".temp");
                client.transferTo(new FileOutputStream(tempFile));

                return new FilePipeRecord(tempFile, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int length; (length = client.read(buffer)) != -1; ) {
                    result.write(buffer, 0, length);
                }

                return new FilePipeRecord(null, result.toString(transferType == TransferTypes.ASCII ? "ASCII" : "Cp1047"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void pipeDataClassToClient(FilePipeRecord data, OutputStream client) {
        if (data.text() != null) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data.text().getBytes(
                    transferType == TransferTypes.ASCII ? "ASCII" :
                            transferType == TransferTypes.EBCDIC ? "Cp1047" : "UTF-8"))) {
                inputStream.transferTo(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (FileInputStream inputStream = new FileInputStream(data.iFile())) {
                inputStream.transferTo(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /*if (transferType == TransferTypes.IMAGE) {

        } else {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data.text().getBytes(transferType == TransferTypes.ASCII ? "ASCII" : "Cp1047"))) {
                inputStream.transferTo(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/
    }
}
