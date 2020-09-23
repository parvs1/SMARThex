# SMARThex
<p align="center">
    <img src="doc/smarthexLogo.png" />
</p>

Overview
--------
<p align="center">
    <img src="doc/image/product.jpg" />
</p>

SMARThex, the Simple Medication Adherence Remembrance Tool, ensures that users take their
medication on time, every time. SMARThex combats the nonadherence epidemic through an
integrated custom pill dispenser and app system.

The App
--------
<p align="center">
    <img src="doc/image/appScreenshots.png" />
</p>
<p align="center">(Screenshots From the SMARThex App)</p>

With a simple and enhanced user interface, the SMARThex app allows users to manage their
medications on their dashboard where they connect to the dispenser and easily input their
medication schedule into the application. The solution boosts adherence through a four-layered
notification system in the app. After linking their medications in the physical dispenser to the
model in the app, a series of four reminders is triggered until the user takes their pill: a push
notification, an alarm, a text, and a phone call. Once the user acknowledges one of the
notifications, the rest are stopped.

The Pill Dispenser
--------
<p align="center">
    <img src="doc/image/pillDispenser.png" />
</p>
<p align="center">(CAD Drawings of the Pill Dispenser Created in Fusion 360)</p>

The pill dispenser is composed of a hexagonal central unit and pill dispenser modules that
connect around the hub. The central module houses a microcontroller and battery, powering the
device. The microcontroller, an Adafruit Feather nRF52 Bluefruit, connects to our application
using Bluetooth, receives the user’s medication schedule, and sends commands to the peripheral
pill dispensers to release medications at a specific time. Each pill dispenser module utilizes a
servo motor to actuate a disk that drops a pill into a tray where the user can simply grab their
medication.

SMARThex In Action!
--------
Check out a [demo](https://photos.app.goo.gl/MzV15pCoprQDqdQj8) of SMARThex where a pill is dispensed when the device connects to the dispenser.
