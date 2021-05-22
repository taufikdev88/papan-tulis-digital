# library untuk tampilan aplikasi
from PyQt5 import QtWidgets
from PyQt5.QtWidgets import QApplication
from PyQt5.QtGui import QPixmap
from PyQt5.QtCore import QTimer, QDate, Qt
# library untuk sistem
from PIL import Image
from io import BytesIO
import sys, ui, base64, asyncio
from asyncqt import QEventLoop

# 1WeKKEadyTKlGZ29EgqO5ndZSwjMpSRdcjJImvAltuCsQJE1pHJUclXmWzXrRTd

class MyApp(QtWidgets.QMainWindow, ui.Ui_MainWindow):
  imgBuffer = ""
  pkgCount = 0

  def __init__(self, parent=None):
    super(MyApp, self).__init__(parent)
    self.setupUi(self)

    with open("open.jpg", "rb") as f:
      b64str = base64.b64encode(f.read()) # f.read() itu bytes -> di encode jadi ke b64str
      self.convertBase64Pic(b64str, False)

  def convertBase64Pic(self, base64str, rotate=True):
    try:
      im = Image.open(BytesIO(base64.b64decode(base64str)))
      if(rotate): 
        im = im.rotate(90, expand=True)
      im.save("file.jpg")
      return True
    except Exception as ex:
      print("Error decode base64: ",str(ex))
      return False

  async def handle_echo(self, reader, writer):
    while True:
      data = await reader.read(4096)
      if not data:
          break

      try:
        message = data.decode('utf-8')

        if("end" in message):
          self.imgBuffer = self.imgBuffer + message[0:message.index("end")]

          if(self.convertBase64Pic(self.imgBuffer)):
            self.updateImage()

          self.imgBuffer = ''
          self.pkgCount = 0
        else:
          self.imgBuffer = self.imgBuffer + message
          self.pkgCount += 1
          
        # writer.write('ok'.encode())
        # await writer.drain()
      except Exception as ex:
        writer.write('ok'.encode())
        await writer.drain()
        
        self.imgBuffer = ''
        self.pkgCount = 0
        print("handle echo error: ",str(ex))
    
    writer.close()

def maintcp():
  app = QApplication(sys.argv)
  loop = QEventLoop(app)
  asyncio.set_event_loop(loop)
  form = MyApp()
  form.show()

  with loop:
    coro = asyncio.start_server(form.handle_echo, '', 5000, loop=loop)
    loop.run_until_complete(coro)
    try:
      loop.run_forever()
    except Exception as ex:
      print("maintcp error: ",str(ex))

if __name__ == '__main__':
  maintcp()