import sys
from ftplib import FTP

from PySide6.QtWidgets import QApplication

from dialogs.connectdialog import ConnectDialog
from dialogs.errordialog import ErrorDialog
from dialogs.explorerdialog import ExplorerDialog
from dialogs.loaderdialog import LoaderDialog

def show_error(error: str, parent=None):
    error_dialog = ErrorDialog(str(error), parent)
    error_dialog.exec()

def get_connection_options():
    connect_dialog = ConnectDialog()
    return_code = connect_dialog.exec()
    return connect_dialog.get_options() if return_code == 1 else None

def prepare_connection(connection_options) -> FTP:
    def inner_ftp_execute():
        ftp = FTP()
        ftp.connect(host=connection_options['server_address'], port=int(connection_options['server_port']))
        if not ftp.login(user=connection_options['user_login'], passwd=connection_options['user_password']).startswith(
                "2"):
            wait_dialog.close()
            raise ConnectionError("Некорректный логин, пароль или адрес")

        if connection_options["is_passive"]:
            ftp.set_pasv(True)
        else:
            ftp.sendport(connection_options["user_address"], int(connection_options["user_port"]))

        if not ftp.lastresp.startswith("2"):
            wait_dialog.close()
            raise ConnectionError("Некорректный логин, пароль или адрес")

        return ftp

    wait_dialog = LoaderDialog(inner_ftp_execute)
    wait_dialog.show()

    return wait_dialog.get_result()

def run_program():
    try:
        while True:
            connection_options = get_connection_options()

            if connection_options is None:
                break

            ftp = prepare_connection(connection_options)

            explorer_dialog = ExplorerDialog(ftp)
            explorer_dialog.exec()
    except Exception as e:
        show_error(str(e))
        raise e


if __name__ == "__main__":
    app = QApplication(sys.argv)
    run_program()
    sys.exit(app.exec())