# -*- coding: utf-8 -*-

################################################################################
## Form generated from reading UI file 'explorer.ui'
##
## Created by: Qt User Interface Compiler version 6.9.0
##
## WARNING! All changes made in this file will be lost when recompiling UI file!
################################################################################

from PySide6.QtCore import (QCoreApplication, QDate, QDateTime, QLocale,
    QMetaObject, QObject, QPoint, QRect,
    QSize, QTime, QUrl, Qt)
from PySide6.QtGui import (QBrush, QColor, QConicalGradient, QCursor,
    QFont, QFontDatabase, QGradient, QIcon,
    QImage, QKeySequence, QLinearGradient, QPainter,
    QPalette, QPixmap, QRadialGradient, QTransform)
from PySide6.QtWidgets import (QApplication, QDialog, QHBoxLayout, QHeaderView,
    QLineEdit, QPushButton, QSizePolicy, QSpacerItem,
    QTableWidget, QTableWidgetItem, QVBoxLayout, QWidget)
import resources_rc

class Ui_Dialog(object):
    def setupUi(self, Dialog):
        if not Dialog.objectName():
            Dialog.setObjectName(u"Dialog")
        Dialog.resize(685, 622)
        self.verticalLayout = QVBoxLayout(Dialog)
        self.verticalLayout.setObjectName(u"verticalLayout")
        self.horizontalLayout = QHBoxLayout()
        self.horizontalLayout.setObjectName(u"horizontalLayout")
        self.buttonDownload = QPushButton(Dialog)
        self.buttonDownload.setObjectName(u"buttonDownload")
        self.buttonDownload.setStyleSheet(u"background-color: rgb(61, 56, 70);")
        icon = QIcon()
        icon.addFile(u":/assets/download.svg", QSize(), QIcon.Mode.Normal, QIcon.State.Off)
        self.buttonDownload.setIcon(icon)
        self.buttonDownload.setIconSize(QSize(32, 32))

        self.horizontalLayout.addWidget(self.buttonDownload)

        self.buttonUpload = QPushButton(Dialog)
        self.buttonUpload.setObjectName(u"buttonUpload")
        self.buttonUpload.setStyleSheet(u"background-color: rgb(61, 56, 70);")
        icon1 = QIcon()
        icon1.addFile(u":/assets/upload.svg", QSize(), QIcon.Mode.Normal, QIcon.State.Off)
        self.buttonUpload.setIcon(icon1)
        self.buttonUpload.setIconSize(QSize(32, 32))

        self.horizontalLayout.addWidget(self.buttonUpload)

        self.buttonCreateDir = QPushButton(Dialog)
        self.buttonCreateDir.setObjectName(u"buttonCreateDir")
        self.buttonCreateDir.setStyleSheet(u"background-color: rgb(61, 56, 70);")
        icon2 = QIcon()
        icon2.addFile(u":/assets/create.svg", QSize(), QIcon.Mode.Normal, QIcon.State.Off)
        self.buttonCreateDir.setIcon(icon2)
        self.buttonCreateDir.setIconSize(QSize(32, 32))

        self.horizontalLayout.addWidget(self.buttonCreateDir)

        self.buttonRemove = QPushButton(Dialog)
        self.buttonRemove.setObjectName(u"buttonRemove")
        self.buttonRemove.setStyleSheet(u"background-color: rgb(61, 56, 70);")
        icon3 = QIcon()
        icon3.addFile(u":/assets/delete.svg", QSize(), QIcon.Mode.Normal, QIcon.State.Off)
        self.buttonRemove.setIcon(icon3)
        self.buttonRemove.setIconSize(QSize(32, 32))

        self.horizontalLayout.addWidget(self.buttonRemove)

        self.buttonRename = QPushButton(Dialog)
        self.buttonRename.setObjectName(u"buttonRename")
        self.buttonRename.setStyleSheet(u"background-color: rgb(61, 56, 70);")
        icon4 = QIcon()
        icon4.addFile(u":/assets/rename.svg", QSize(), QIcon.Mode.Normal, QIcon.State.Off)
        self.buttonRename.setIcon(icon4)
        self.buttonRename.setIconSize(QSize(32, 32))

        self.horizontalLayout.addWidget(self.buttonRename)

        self.horizontalSpacer = QSpacerItem(40, 20, QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Minimum)

        self.horizontalLayout.addItem(self.horizontalSpacer)


        self.verticalLayout.addLayout(self.horizontalLayout)

        self.inputCurrentDir = QLineEdit(Dialog)
        self.inputCurrentDir.setObjectName(u"inputCurrentDir")

        self.verticalLayout.addWidget(self.inputCurrentDir)

        self.contents = QTableWidget(Dialog)
        if (self.contents.columnCount() < 3):
            self.contents.setColumnCount(3)
        __qtablewidgetitem = QTableWidgetItem()
        __qtablewidgetitem.setTextAlignment(Qt.AlignLeading|Qt.AlignVCenter);
        self.contents.setHorizontalHeaderItem(0, __qtablewidgetitem)
        __qtablewidgetitem1 = QTableWidgetItem()
        __qtablewidgetitem1.setTextAlignment(Qt.AlignLeading|Qt.AlignVCenter);
        self.contents.setHorizontalHeaderItem(1, __qtablewidgetitem1)
        __qtablewidgetitem2 = QTableWidgetItem()
        __qtablewidgetitem2.setTextAlignment(Qt.AlignLeading|Qt.AlignVCenter);
        self.contents.setHorizontalHeaderItem(2, __qtablewidgetitem2)
        self.contents.setObjectName(u"contents")
        self.contents.setColumnCount(3)
        self.contents.horizontalHeader().setStretchLastSection(True)

        self.verticalLayout.addWidget(self.contents)


        self.retranslateUi(Dialog)

        QMetaObject.connectSlotsByName(Dialog)
    # setupUi

    def retranslateUi(self, Dialog):
        Dialog.setWindowTitle(QCoreApplication.translate("Dialog", u"\u041f\u0440\u043e\u0432\u043e\u0434\u043d\u0438\u043a", None))
#if QT_CONFIG(tooltip)
        self.buttonDownload.setToolTip(QCoreApplication.translate("Dialog", u"\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0444\u0430\u0439\u043b", None))
#endif // QT_CONFIG(tooltip)
        self.buttonDownload.setText("")
#if QT_CONFIG(tooltip)
        self.buttonUpload.setToolTip(QCoreApplication.translate("Dialog", u"\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u0444\u0430\u0439\u043b", None))
#endif // QT_CONFIG(tooltip)
        self.buttonUpload.setText("")
#if QT_CONFIG(tooltip)
        self.buttonCreateDir.setToolTip(QCoreApplication.translate("Dialog", u"\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043f\u0430\u043f\u043a\u0443", None))
#endif // QT_CONFIG(tooltip)
        self.buttonCreateDir.setText("")
#if QT_CONFIG(tooltip)
        self.buttonRemove.setToolTip(QCoreApplication.translate("Dialog", u"\u0423\u0434\u0430\u043b\u0438\u0442\u044c", None))
#endif // QT_CONFIG(tooltip)
        self.buttonRemove.setText("")
#if QT_CONFIG(tooltip)
        self.buttonRename.setToolTip(QCoreApplication.translate("Dialog", u"\u041f\u0435\u0440\u0435\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u0442\u044c", None))
#endif // QT_CONFIG(tooltip)
        self.buttonRename.setText("")
        ___qtablewidgetitem = self.contents.horizontalHeaderItem(0)
        ___qtablewidgetitem.setText(QCoreApplication.translate("Dialog", u"\u0418\u043c\u044f", None));
        ___qtablewidgetitem1 = self.contents.horizontalHeaderItem(1)
        ___qtablewidgetitem1.setText(QCoreApplication.translate("Dialog", u"\u0420\u0430\u0437\u043c\u0435\u0440", None));
        ___qtablewidgetitem2 = self.contents.horizontalHeaderItem(2)
        ___qtablewidgetitem2.setText(QCoreApplication.translate("Dialog", u"\u041f\u043e\u0441\u043b\u0435\u0434\u043d\u0435\u0435 \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435", None));
    # retranslateUi

