from PySide6.QtWidgets import QDialog
from ftplib import FTP

from dialogs.errordialog import ErrorDialog
from uicompiled.explorer import Ui_Dialog as UiExplorer
import resources

def show_error(error: str, parent=None):
    error_dialog = ErrorDialog(str(error), parent)
    error_dialog.exec()

class ExplorerDialog(QDialog):
    def __init__(self, ftp: FTP, parent=None):
        super().__init__(parent)
        self.ui = UiExplorer()
        self.ui.setupUi(self)
        self.__ftp = ftp
        self.ui.inputCurrentDir.returnPressed.connect(self.change_directory)
        self.update_list()

    def update_list(self):
        self.ui.inputCurrentDir.setText(self.__ftp.pwd())
        self.__ftp.dir()

    def change_directory(self):
        if not self.__ftp.cwd(self.ui.inputCurrentDir.text()).startswith("2"):
            show_error(f"Невозможно переключиться на директорию '{self.ui.inputCurrentDir.text()}'")

        self.update_list()