sudo apt update -y
sudo apt upgrade -y
sudo apt install python3-pyqt5 -y
pip3 install asyncqt
pip3 install Pillow
curl -s https://install.zerotier.com | sudo bash
sudo zerotier-cli join 93afae59639d273c

# installasi di raspberry pi
# pindah di folder /home/pi dan rename agar gampang
# lalu buat sebuah file .sh di /home/pi
# 
# nano run.sh
#
# isinya:
# cd /home/pi/*namafolderaplikasi*
# python3 main.py