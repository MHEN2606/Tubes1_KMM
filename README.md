# Tubes1_KMM
Tugas Besar 1 - Strategi Algoritma

Dibuat oleh:
| NIM | Nama |
|:---:|:----:|
|13521006 | Azmi Hasna Zahrani |
|13521007 | Matthew Mahendra |
|13521018 | Syarifa Dwi Purnamasari|

# Nama Program
Galaxio Bot with Java

## Algoritma yang Digunakan
Bot ini menggunakan algoritma greedy dalam menentukan setiap aksi yang dilakukannya. Greedy yang digunakan pada bot ini adalah greedy by position, sehingga yang menjadi pertimbangan adalah pada posisi baik dari bot maupun objek-objek lainnya termasuk musuh. 

Aksi default yang dilakukan adalah memakan makanan untuk bertambah besar. Selanjutnya jika pada posisi bot ada objek gas cloud atau asteroid yang dekat, maka dilakukan proses menghindari objek tersebut.

Pada kasus terdapat musuh yang dekat, maka aksi ditentukan berdasarkan ukuran dari bot dan ukuran dari musuh. Jika bot lebih kecil maka langkah yang diambil adalah untuk menghindar dan jika jaraknya terlalu dekat serta size bot memungkinkan, menggunakan afterburner. Sebaliknya, jika bot lebih beasar dari musuh yang paling dekat, maka dilakukan penyerangan dengan mengarahkan diri ke musuh serta jika memungkinkan melakukan penembakan torpedo ataupun teleport agar lebih dekat.

Pada kasus terdapat torpedo yang dekat, maka bot akan menghindar dan mengaktifkan shield.

Pada kasus bot berada di ujung peta, maka bot akan kembali ke dalam peta dengan menggunakan titik pusat peta sebagai tujuannya.

# Prerequisites Menjalankan
Untuk menjalankan bot ini Anda memerlukan,
1. Java SDK (minimal versi 11. Bot dikembangkan pada versi 19.0.1)
2. .NET 3.1.0 dan .NET Runtime 5.0 untuk menjalankan game engine
3. Maven untuk membuat file .JAR
4. Game Engine yang dapat diunduh pada tautan: <a> https://github.com/EntelectChallenge/2021-Galaxio/releases/tag/2021.3.2 </a>

# Cara menjalankan Bot
1. Unduh seluruh repository ini sebuah folder dan letakkan dalam engine yang telah diunduh pada folder starter-bots
2. TBC