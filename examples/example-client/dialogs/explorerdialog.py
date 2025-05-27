import os
import re
from ftplib import FTP
from os.path import abspath

from PySide6.QtWidgets import QDialog, QTableWidgetItem, QAbstractItemView, QFileDialog

from dialogs.createdialog import CreateDialog
from dialogs.errordialog import ErrorDialog
from dialogs.loaderdialog import LoaderDialog
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
        self.ui.contents.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers)
        self.ui.contents.cellDoubleClicked.connect(self.cell_double_clicked)
        self.ui.buttonDownload.clicked.connect(self.button_download_clicked)
        self.ui.buttonUpload.clicked.connect(self.upload_file)
        self.ui.buttonCreateDir.clicked.connect(self.mkdir)
        self.__is_folder = []
        self.update_list()

    def update_list(self):
        self.ui.inputCurrentDir.setText(self.__ftp.pwd())

        current_index = -1

        def line_extractor(line: str):
            if line.startswith("total"):
                self.ui.contents.clearContents()
                self.ui.contents.setRowCount(0)
                self.__is_folder = []
                return
            nonlocal current_index

            if current_index == -1:
                current_index += 1
                return

            line_splitted = re.split(r'[ ]+', line)
            self.ui.contents.insertRow(self.ui.contents.rowCount())
            self.ui.contents.setItem(current_index, 0, QTableWidgetItem(line_splitted[-1]))
            self.ui.contents.setItem(current_index, 1,
                                     QTableWidgetItem("" if line_splitted[0].startswith("d") else line_splitted[4]))
            self.ui.contents.setItem(current_index, 2, QTableWidgetItem(" ".join(line_splitted[5:8])))
            self.__is_folder.append(line_splitted[0].startswith("d"))
            current_index += 1

        self.__ftp.retrlines("LIST", line_extractor)

    def change_directory(self, new_place: str = None):
        if new_place is not None:
            self.ui.inputCurrentDir.setText(new_place)

        self.ui.inputCurrentDir.setText(abspath(self.ui.inputCurrentDir.text()))

        if not self.__ftp.cwd(self.ui.inputCurrentDir.text()).startswith("2"):
            show_error(f"Невозможно переключиться на директорию '{self.ui.inputCurrentDir.text()}'")

        self.update_list()

    def cell_double_clicked(self):
        selected_items = self.ui.contents.selectedItems()
        if len(selected_items) == 0:
            return

        selected_item = selected_items[0]

        if self.__is_folder[selected_item.row()]:
            self.change_directory(
                f"{self.ui.inputCurrentDir.text()}/{self.ui.contents.item(selected_item.row(), 0).text()}")
        else:
            self.save_file(self.ui.contents.item(selected_item.row(), 0).text())

    def button_download_clicked(self):
        selected_items = self.ui.contents.selectedItems()
        if len(selected_items) == 0:
            show_error("Выберите файл для сохранения в таблице")
            return

        selected_item = selected_items[0]
        self.save_file(self.ui.contents.item(selected_item.row(), 0).text())

    def save_file(self, file_name):
        (save_file_name, _) = QFileDialog.getSaveFileName(self,
                                                          "Сохранить файл",
                                                          os.getcwd() + "/" + file_name,
                                                          "Все файлы (*)"
                                                          )

        if save_file_name == "":
            return

        def inner_func():
            with open(save_file_name, "wb") as fp:
                self.__ftp.retrbinary(f"RETR {file_name}", fp.write)

        try:
            loader_dialog = LoaderDialog(inner_func)
            loader_dialog.show()
            loader_dialog.get_result()
        except Exception as e:
            show_error(str(e))
            loader_dialog.close()

    def upload_file(self):
        (upload_file_path, _) = QFileDialog.getOpenFileName(self,
                                                            "Загрузить файл",
                                                            os.getcwd(),
                                                            "Все файлы (*)"
                                                            )

        def inner_func():
            with open(upload_file_path, "rb") as fp:
                self.__ftp.storbinary(f"STOU {upload_file_path.split('/')[-1]}", fp)

        try:
            loader_dialog = LoaderDialog(inner_func)
            loader_dialog.show()
            loader_dialog.get_result()
        except Exception as e:
            show_error(str(e))
            loader_dialog.close()

        self.update_list()

    def mkdir(self):
        create_dir = CreateDialog(self)
        return_code = create_dir.exec()
        if return_code == QDialog.DialogCode.Rejected:
            return

        self.__ftp.mkd(create_dir.get_name())
        self.update_list()