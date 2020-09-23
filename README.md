# What is SMARThex?
SMARThex, the Simple Medication Adherence Remembrance Tool, ensures that users take their
medication on time, every time. SMARThex combats the nonadherence epidemic through an
integrated, custom pill dispenser and app system.

The pill dispenser is composed of a hexagonal central unit and pill dispenser modules that
connect around the hub. The central module houses a microcontroller and battery, powering the
device. The microcontroller, an Adafruit Feather nRF52 Bluefruit, connects to our application
using Bluetooth, receives the user’s medication schedule, and sends commands to the peripheral
pill dispensers to release medications at a specific time. Each pill dispenser module utilizes a
servo motor to actuate a disk that drops a pill into a tray where the user can simply grab their
medication.

Our app, with a simple and enhanced user interface, allows users to manage their
medications on their dashboard where they connect to the dispenser and easily input their
medication schedule into the application. The solution boosts adherence through a four-layered
notification system in the app. After linking their medications in the physical dispenser to the
model in the app, a series of four reminders is triggered until the user takes their pill: a push
notification, an alarm, a text, and a phone call. Once the user acknowledges one of the
notifications, the rest are stopped.

When it is time to take their medication, the user scans their phone with the NFC tag on the
dispenser to record that they are taking the correct dosage, creating data of their medication
adherence pattern to be used by doctors and pharmacists for later analysis. At the same time,
the dispenser automatically releases the requisite dosage of medicine. NFC (Near Field
Communication) technology is a bluetooth-powered chip located on the back of modern
smartphones. When contact is made between two NFC tags, a bluetooth connection is
established and both devices can communicate with each other. By integrating this technology
into the SMARThex dispenser and mobile app, we ensured that each user needs to make
deliberate contact between their SMARThex device and their own smartphone to bypass the
four-layered redundancy system, making it nearly impossible for users to neglect taking their
medicine.
