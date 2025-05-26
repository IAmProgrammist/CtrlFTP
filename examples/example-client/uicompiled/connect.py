# -*- coding: utf-8 -*-

################################################################################
## Form generated from reading UI file 'connect.ui'
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
from PySide6.QtWidgets import (QApplication, QCheckBox, QDialog, QLabel,
    QLineEdit, QPushButton, QSizePolicy, QSpacerItem,
    QVBoxLayout, QWidget)

class Ui_Dialog(object):
    def setupUi(self, Dialog):
        if not Dialog.objectName():
            Dialog.setObjectName(u"Dialog")
        Dialog.resize(647, 624)
        self.verticalLayout_2 = QVBoxLayout(Dialog)
        self.verticalLayout_2.setObjectName(u"verticalLayout_2")
        self.verticalLayout = QVBoxLayout()
        self.verticalLayout.setObjectName(u"verticalLayout")
        self.label = QLabel(Dialog)
        self.label.setObjectName(u"label")
        self.label.setStyleSheet(u"font: 700 14pt \"Arial\";")

        self.verticalLayout.addWidget(self.label)

        self.label_2 = QLabel(Dialog)
        self.label_2.setObjectName(u"label_2")

        self.verticalLayout.addWidget(self.label_2)

        self.inputServerAddress = QLineEdit(Dialog)
        self.inputServerAddress.setObjectName(u"inputServerAddress")

        self.verticalLayout.addWidget(self.inputServerAddress)

        self.label_3 = QLabel(Dialog)
        self.label_3.setObjectName(u"label_3")

        self.verticalLayout.addWidget(self.label_3)

        self.inputServerPort = QLineEdit(Dialog)
        self.inputServerPort.setObjectName(u"inputServerPort")

        self.verticalLayout.addWidget(self.inputServerPort)


        self.verticalLayout_2.addLayout(self.verticalLayout)

        self.verticalSpacer = QSpacerItem(20, 40, QSizePolicy.Policy.Minimum, QSizePolicy.Policy.Expanding)

        self.verticalLayout_2.addItem(self.verticalSpacer)

        self.verticalLayout_3 = QVBoxLayout()
        self.verticalLayout_3.setObjectName(u"verticalLayout_3")
        self.label_4 = QLabel(Dialog)
        self.label_4.setObjectName(u"label_4")
        self.label_4.setStyleSheet(u"font: 700 14pt \"Arial\";")

        self.verticalLayout_3.addWidget(self.label_4)

        self.inputIsPassive = QCheckBox(Dialog)
        self.inputIsPassive.setObjectName(u"inputIsPassive")

        self.verticalLayout_3.addWidget(self.inputIsPassive)

        self.label_6 = QLabel(Dialog)
        self.label_6.setObjectName(u"label_6")

        self.verticalLayout_3.addWidget(self.label_6)

        self.inputDataPort = QLineEdit(Dialog)
        self.inputDataPort.setObjectName(u"inputDataPort")

        self.verticalLayout_3.addWidget(self.inputDataPort)


        self.verticalLayout_2.addLayout(self.verticalLayout_3)

        self.verticalSpacer_2 = QSpacerItem(20, 40, QSizePolicy.Policy.Minimum, QSizePolicy.Policy.Expanding)

        self.verticalLayout_2.addItem(self.verticalSpacer_2)

        self.verticalLayout_4 = QVBoxLayout()
        self.verticalLayout_4.setObjectName(u"verticalLayout_4")
        self.label_5 = QLabel(Dialog)
        self.label_5.setObjectName(u"label_5")
        self.label_5.setStyleSheet(u"font: 700 14pt \"Arial\";")

        self.verticalLayout_4.addWidget(self.label_5)

        self.label_7 = QLabel(Dialog)
        self.label_7.setObjectName(u"label_7")

        self.verticalLayout_4.addWidget(self.label_7)

        self.inputLogin = QLineEdit(Dialog)
        self.inputLogin.setObjectName(u"inputLogin")

        self.verticalLayout_4.addWidget(self.inputLogin)

        self.label_8 = QLabel(Dialog)
        self.label_8.setObjectName(u"label_8")

        self.verticalLayout_4.addWidget(self.label_8)

        self.inputPassword = QLineEdit(Dialog)
        self.inputPassword.setObjectName(u"inputPassword")

        self.verticalLayout_4.addWidget(self.inputPassword)


        self.verticalLayout_2.addLayout(self.verticalLayout_4)

        self.verticalSpacer_3 = QSpacerItem(20, 40, QSizePolicy.Policy.Minimum, QSizePolicy.Policy.Expanding)

        self.verticalLayout_2.addItem(self.verticalSpacer_3)

        self.buttonConnect = QPushButton(Dialog)
        self.buttonConnect.setObjectName(u"buttonConnect")

        self.verticalLayout_2.addWidget(self.buttonConnect)


        self.retranslateUi(Dialog)

        QMetaObject.connectSlotsByName(Dialog)
    # setupUi

    def retranslateUi(self, Dialog):
        Dialog.setWindowTitle(QCoreApplication.translate("Dialog", u"\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u043a \u0441\u0435\u0440\u0432\u0435\u0440\u0443", None))
        self.label.setText(QCoreApplication.translate("Dialog", u"\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u043a \u0441\u0435\u0440\u0432\u0435\u0440\u0443", None))
        self.label_2.setText(QCoreApplication.translate("Dialog", u"\u0410\u0434\u0440\u0435\u0441", None))
        self.label_3.setText(QCoreApplication.translate("Dialog", u"\u041f\u043e\u0440\u0442", None))
        self.label_4.setText(QCoreApplication.translate("Dialog", u"\u0414\u0430\u0442\u0430-\u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435", None))
        self.inputIsPassive.setText(QCoreApplication.translate("Dialog", u"\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u044b\u0439 \u0440\u0435\u0436\u0438\u043c", None))
        self.label_6.setText(QCoreApplication.translate("Dialog", u"\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u043c\u044b\u0439 \u043f\u043e\u0440\u0442", None))
        self.label_5.setText(QCoreApplication.translate("Dialog", u"\u0414\u0430\u043d\u043d\u044b\u0435 \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f", None))
        self.label_7.setText(QCoreApplication.translate("Dialog", u"\u041b\u043e\u0433\u0438\u043d", None))
        self.label_8.setText(QCoreApplication.translate("Dialog", u"\u041f\u0430\u0440\u043e\u043b\u044c", None))
        self.buttonConnect.setText(QCoreApplication.translate("Dialog", u"\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0438\u0442\u044c\u0441\u044f", None))
    # retranslateUi

