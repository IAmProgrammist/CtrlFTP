import sys
from ftplib import FTP
from typing import Optional

from PySide6.QtWidgets import QApplication

from dialogs.connectdialog import ConnectDialog
from dialogs.loaderdialog import LoaderDialog

def get_connection_options():
    connect_dialog = ConnectDialog()
    connect_dialog.exec()
    return connect_dialog.get_options()

def prepare_connection(connection_options) -> Optional[FTP]:
    # Показать всплывашку с загрузкой
    wait_dialog = LoaderDialog()
    wait_dialog.show()

    ftp = FTP(connection_options[""])

    # Закрыть всплывашку с загрузкой
    wait_dialog.close()

if __name__ == "__main__":
    app = QApplication(sys.argv)

    # Получить опции для подключения сервера
    connection_options = get_connection_options()

    # Выполнить подключение к серверу
    prepare_connection(connection_options)

    sys.exit(app.exec())