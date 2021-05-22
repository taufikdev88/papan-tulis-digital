# from PyQt5.QtCore import QDate, Qt

# if __name__ == '__main__':
#   now = QDate.currentDate()

#   print(now.toString(Qt.DefaultLocaleLongDate)) 

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_MainWindow(object):
  def setupUi(self, MainWindow):
    MainWindow.setObjectName("MainWindow")
    MainWindow.setWindowState(QtCore.Qt.WindowMaximized)
    sizePolicy = QtWidgets.QSizePolicy(QtWidgets.QSizePolicy.Fixed, QtWidgets.QSizePolicy.Fixed)
    sizePolicy.setHorizontalStretch(0)
    sizePolicy.setVerticalStretch(0)
    sizePolicy.setHeightForWidth(MainWindow.sizePolicy().hasHeightForWidth())
    MainWindow.setSizePolicy(sizePolicy)
    self.centralwidget = QtWidgets.QWidget(MainWindow)
    self.centralwidget.setObjectName("CentraWidget")
    MainWindow.setCentralWidget(self.centralwidget)
    self.statusbar = QtWidgets.QStatusBar(MainWindow)
    self.statusbar.setObjectName("StatusBar")
    MainWindow.setStatusBar(self.statusbar)
    self.lblImage = QtWidgets.QLabel(self.centralwidget)
    self.lblImage.setAlignment(QtCore.Qt.AlignCenter)
    self.lblImage.resize(self.width(), self.height())
    self.lblImage.setMinimumSize(100,200)
    self.lblImage.setText("Hall")
    self.lblImage.setObjectName("lblImage")

    self.retranslateUi(MainWindow)
    QtCore.QMetaObject.connectSlotsByName(MainWindow)

  def retranslateUi(self, MainWindow):
    _translate = QtCore.QCoreApplication.translate
    MainWindow.setWindowTitle(_translate("MainWindow", "Papan Tulis Digital"))

  def resizeEvent(self, event):
    self.updateImage()

  def updateImage(self):
    is_convert_success = False
    try:
      pixmap = QtGui.QPixmap("file.jpg")

      if(self.width() < self.height()):
        pixmap = pixmap.scaledToWidth(self.width())
      elif(self.width() > self.height()):
        pixmap = pixmap.scaledToHeight(self.height())
      else:
        pixmap = pixmap.scaled(self.width(), self.height())
      is_convert_success = True
    except Exception as ex:
      print("error ui: ", str(ex))
      is_convert_success = False
    
    if(is_convert_success):
      self.lblImage.setPixmap(pixmap)
      self.lblImage.resize(self.width(), self.height())