import sys

from PySide6.QtWidgets import QApplication

from dialogs.connect import Connect

if __name__ == "__main__":
    app = QApplication(sys.argv)

    conect_dialog = Connect()
    response = conect_dialog.exec()

    sys.exit(app.exec())