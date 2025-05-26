import sys
from ftplib import FTP
from typing import Optional

from PySide6.QtWidgets import QApplication

from dialogs.connectdialog import ConnectDialog
from dialogs.errordialog import ErrorDialog
from dialogs.loaderdialog import LoaderDialog

def show_error(error: Exception, parent=None):
    error_dialog = ErrorDialog(str(error), parent)
    error_dialog.exec()

def get_connection_options():
    connect_dialog = ConnectDialog()
    connect_dialog.exec()
    return connect_dialog.get_options()

def prepare_connection(connection_options) -> Optional[FTP]:
    # Показать всплывашку с загрузкой
    wait_dialog = LoaderDialog()
    wait_dialog.show()

    ftp = FTP(f"{connection_options['server_address']}:{connection_options['server_port']}",
              connection_options['user_login'], connection_options['user_password'])

    ftp.login()
    if not ftp.lastresp.startswith("2"):
        raise ConnectionError("Некорректный логин, пароль или адрес")

    # Закрыть всплывашку с загрузкой
    wait_dialog.close()

if __name__ == "__main__":
    app = QApplication(sys.argv)

    try:
        # Получить опции для подключения сервера
        connection_options = get_connection_options()

        # Выполнить подключение к серверу
        prepare_connection(connection_options)
    except:
        show_error("Некорректный логин, пароль или адрес")


    sys.exit(app.exec())