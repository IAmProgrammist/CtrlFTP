import sys
from ftplib import FTP
from typing import Optional
import resources

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
    wait_dialog = LoaderDialog()
    wait_dialog.show()

    ftp = FTP(f"{connection_options['server_address']}:{connection_options['server_port']}",
              connection_options['user_login'], connection_options['user_password'])
    if not ftp.login().startswith("2"):
        raise ConnectionError("Некорректный логин, пароль или адрес")

    if connection_options["is_passive"]:
        ftp.set_pasv(True)
    else:
        ftp.sendport(connection_options["user_address"], int(connection_options["use_port"]))

    if not ftp.lastresp.startswith("2"):
        raise ConnectionError("Некорректный логин, пароль или адрес")

    wait_dialog.close()

    return ftp

def run_program():
    try:
        connection_options = get_connection_options()

        if connection_options == None:
            return

        ftp = prepare_connection(connection_options)

        explorer_dialog = ExplorerDialog(ftp)
        explorer_dialog.exec()
    except:
        show_error("Некорректный логин, пароль или адрес")


if __name__ == "__main__":
    app = QApplication(sys.argv)
    run_program()
    sys.exit(app.exec())