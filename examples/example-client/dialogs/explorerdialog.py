from PySide6.QtWidgets import QDialog, QLabel, QTableWidgetItem
from ftplib import FTP
import re

from dialogs.errordialog import ErrorDialog
from uicompiled.explorer import Ui_Dialog as UiExplorer

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

        current_index = 0

        def line_extractor(line: str):
            if line.startswith("total"):
                self.ui.contents.setRowCount(int(line.split()[1]))
                return
            nonlocal current_index
            line_splitted = re.split(r'[^ ]+', line)
            self.ui.contents.setItem(current_index, 0, QTableWidgetItem(line_splitted[-1]))
            self.ui.contents.setItem(current_index, 1, QTableWidgetItem(line_splitted[4]))
            self.ui.contents.setItem(current_index, 2, QTableWidgetItem(" ".join(line_splitted[5:8])))
            current_index += 1

        self.__ftp.retrlines("LIST", line_extractor)
        self.__ftp.dir()


    def change_directory(self):
        if not self.__ftp.cwd(self.ui.inputCurrentDir.text()).startswith("2"):
            show_error(f"Невозможно переключиться на директорию '{self.ui.inputCurrentDir.text()}'")

        self.update_list()