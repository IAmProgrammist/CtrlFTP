import os
import re
from ftplib import FTP
from os.path import abspath

from PySide6.QtWidgets import QDialog, QTableWidgetItem, QAbstractItemView, QFileDialog

from dialogs.createdialog import CreateDialog
from dialogs.errordialog import ErrorDialog
from dialogs.loaderdialog import LoaderDialog
from dialogs.renamedialog import RenameDialog
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
        self.ui.contents.setSelectionBehavior(QAbstractItemView.SelectionBehavior.SelectRows)
        self.ui.buttonDownload.clicked.connect(self.button_download_clicked)
        self.ui.buttonUpload.clicked.connect(self.button_upload_clicked)
        self.ui.buttonCreateDir.clicked.connect(self.button_folder_created_clicked)
        self.ui.buttonRemove.clicked.connect(self.button_delete_clicked)
        self.ui.buttonRename.clicked.connect(self.button_rename_clicked)
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

            line_split = re.split(r'[ ]+', line)
            self.ui.contents.insertRow(self.ui.contents.rowCount())
            self.ui.contents.setItem(current_index, 0, QTableWidgetItem(line_split[-1]))
            self.ui.contents.setItem(current_index, 1,
                                     QTableWidgetItem("" if line_split[0].startswith("d") else line_split[4]))
            self.ui.contents.setItem(current_index, 2, QTableWidgetItem(" ".join(line_split[5:8])))
            self.__is_folder.append(line_split[0].startswith("d"))
            current_index += 1

        self.__ftp.retrlines("LIST", line_extractor)

    def change_directory(self, new_place: str = None):
        if new_place is not None:
            self.ui.inputCurrentDir.setText(new_place)

        self.ui.inputCurrentDir.setText(abspath(self.ui.inputCurrentDir.text()))

        try:
            self.__ftp.cwd(self.ui.inputCurrentDir.text())
        except Exception as e:
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
        if self.__is_folder[selected_item.row()]:
            show_error("Невозможно сохранить папку, выберите файл")
            return

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

        loader_dialog = LoaderDialog(inner_func)
        try:
            loader_dialog.show()
            loader_dialog.get_result()
        except Exception as e:
            show_error(str(e))
            loader_dialog.close()

    def button_upload_clicked(self):
        (upload_file_path, _) = QFileDialog.getOpenFileName(self,
                                                            "Загрузить файл",
                                                            os.getcwd(),
                                                            "*.*"
                                                            )

        if upload_file_path == "":
            return

        def inner_func():
            with open(upload_file_path, "rb") as fp:
                self.__ftp.storbinary(f"STOU {upload_file_path.split('/')[-1]}", fp)

        loader_dialog = LoaderDialog(inner_func)
        try:
            loader_dialog.show()
            loader_dialog.get_result()
        except Exception as e:
            show_error(str(e))
            loader_dialog.close()

        self.update_list()

    def button_folder_created_clicked(self):
        create_dir = CreateDialog(self)
        return_code = create_dir.exec()
        if return_code == QDialog.DialogCode.Rejected:
            return

        self.__ftp.mkd(create_dir.get_name())
        if not self.__ftp.lastresp.startswith("2"):
            show_error(f"Не удалось создать папку")

        self.update_list()

    def button_delete_clicked(self):
        selected_items = self.ui.contents.selectedItems()
        if len(selected_items) == 0:
            show_error("Выберите файл или папку для удаления в таблице")
            return

        selected_item = selected_items[0]
        if self.__is_folder[selected_item.row()]:
            self.delete_folder(self.ui.contents.item(selected_item.row(), 0).text())
        else:
            self.delete_file(self.ui.contents.item(selected_item.row(), 0).text())

    def delete_file(self, file_name: str):
        self.__ftp.delete(file_name)
        if not self.__ftp.lastresp.startswith("2"):
            show_error(f"Не удалось удалить файл {self.__ftp.lastresp}")

        self.update_list()

    def delete_folder(self, folder_name: str):
        self.__ftp.rmd(folder_name)
        if not self.__ftp.lastresp.startswith("2"):
            show_error(f"Не удалось удалить папку {self.__ftp.lastresp}")

        self.update_list()

    def button_rename_clicked(self):
        selected_items = self.ui.contents.selectedItems()
        if len(selected_items) == 0:
            show_error("Выберите файл или папку для переименования в таблице")
            return

        selected_item = selected_items[0]

        rename_dialog = RenameDialog(self.ui.contents.item(selected_item.row(), 0).text(), self)
        rename_dialog.exec()

        self.__ftp.rename(self.ui.contents.item(selected_item.row(), 0).text(),
                                     rename_dialog.get_name())
        if not self.__ftp.lastresp.startswith("2"):
            show_error(f"Не удалось переименовать {self.ui.contents.item(selected_item.row(), 0)} в {rename_dialog.get_name()}")

        self.update_list()
