===================================
VGI (Vaucanson Graphical Interface)
http://code.google.com/p/vgi/
Nov. 13, 2011
===================================

VGI is a graphical user interface (GUI) for the Vaucanson library, a finite state machine manipulation platform.


============
Requirements
============

- Linux-like OS (Tested on Mac OS X 10.6.8 and Ubuntu)
- Vaucanson library installed (sudo make install)
  - Available from http://www.lrde.epita.fr/cgi-bin/twiki/view/Vaucanson/WebHome
  - Latest version tested: 1.4
- Netbeans IDE 7.0.1 with the C/C++ plugins installed
- Latest JDK (Java Development Kit)


===========
Quick Start
===========

Executable binaries of VGI are not currently available, but source codes are provided and can be compiled into executable forms relatively quickly assuming you meet the requirements.

1. If you are reading this file, you likely have already obtained the VGI source codes from http://code.google.com/p/vgi/.  If not, use the command "git clone https://code.google.com/p/vgi/" to get a copy of the source codes.

2. There are two Netbeans projects in the downloaded source codes, vgi and vjni.  vgi is a Java project and contains most of the source codes for VGI.  vjni is a C++ dynamic library used in VGI to call functions in the Vaucanson library.  vjni is the glue that connects VGI (written in Java) and the Vaucanson library (written in C++).

3. Open vjni in Netbeans.  Select the "Run" menu and then select the "Clean and Build Project" menu option.  If the Vaucanson library is installed, the build should succeed and vjni.so will be generated under the vjni folder.

4. Open vgi in Netbeans.  Select the "Run" menu and then select the "Clean and Build Project" menu option.  After the build succeeds, select the "Run" menu and then select the "Run Project" menu option.  The VGI window should appear and you may try out the available functions.


=================
Important Warning
=================

VGI is quite experimental and lacks regular and extensive testing.  Some functions may not work as expected and sometimes crash the program.  A list of known bugs is going to be updated in the issues section of http://code.google.com/p/vgi/ and addressed as the project progresses.
