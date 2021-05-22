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
# -> cd /home/pi 
# -> nano run.sh
#
# isinya:
#
# cd /home/pi/*namafolderaplikasi*
# python3 main.py
# 
# simpan dan keluar (ctrl+o , ctrl+x)
# set agar file sh bisa di jalankan dengan cara
# -> chmod +x run.sh
#
# set autostart nya
# -> sudo nano /etc/xdg/lxsession/LXDE-pi/autostart
#
# tambahkan di paling bawah
#
# @/home/pi/run.sh