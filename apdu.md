# mifareClassic & APDU
MIFARE Classic APDU commands pairs examples

In the context of smart cards, an application protocol data unit (APDU) is the communication unit between a smart card reader and a smart card. The structure of the APDU is defined by ISO/IEC 7816-4 Organization, security and commands for interchange.

Command-response pairs
----------------------
A command-response pair, namely a command APDU followed by a response APDU in the opposite direction (see ISO/IEC 7816-3). There shall be no interleaving of command-response pairs across the interface, i.e., the response APDU shall be received before initiating another command-response pair.


| FIELD  | DESCRIPTION        | NUMBER OF BYTES |
|:-------|:-------------------|:---------------:|
|CMDHead |CLA:Class Byte      |1                |
|CMDHead |INS:Instruction Byte|1                |
|CMDHead |P1/P2:Parameter Byte|2                |
|LcField |Lenght of DataComand|0, 1 o 3         |
|CDField |Command Data if Lc>0|Nc               |
|LeField |Lenght of Data      |0, 1, 2 o 3      |

| FIELD  | DESCRIPTION        | NUMBER OF BYTES |
|:-------|:-------------------|:---------------:|
|RDataFd |SW1/SW2:Status Bytes|2                |

If the process is aborted, then the card may become unresponsive. However if a response APDU occurs, then the response data field shall be absent and SW1-SW2 shall indicate an error.
P1-P2 indicates controls and options for processing the command. A parameter byte set to '00' generally provides no further qualification. There is no other general convention for encoding the parameter bytes.

General conventions are specified hereafter for encoding the class byte denoted CLA, the instruction byte denoted INS and the status bytes denoted SW1-SW2). In those bytes, the RFU bits shall be set to 0 unless otherwise specified. To see the specific commands and error responses see the ISO / IEC 7816-4 standard.

Examples
--------

Load MIFARE Classic Key #FF FF FF FF FF FF
|   CLA   |   INS   |    P1    |   P2   |   Lc   | DataB1 | DataB2 | DataB3 | DataB4 | DataB5 | DataB6 |   
|:-------:|:-------:|:--------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|
|FFh      |82h      |KStructure|KNumber |Lenght  | DataB1 | DataB2 | DataB3 | DataB4 | DataB5 | DataB6 |      
|FFh      |82h      |00h       |00h     |06h     | FFh    | FFh    |FFh     | FFh    | FFh    | FFh    |


Authenticate MIFARE Classic Block #04 with loaded Key type B
|   CLA   |   INS   |   P1   |   P2   |   Lc   | DataB1 | DataB2 | DataB3 | DataB4 | DataB5 |  
|:-------:|:-------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|
|FFh      |86h      |00h     |00h     |05h     |Version |00h     |Block N°|KeyType |Key N°  |
|FFh      |86h      |00h     |00h     |05h     |01h     |00h     |04h     |61h     |00      |


Read MIFARE Classic Block #04 all 16 Bytes
|   CLA   |   INS   |   P1   |   P2   |   Le   |
|:-------:|:-------:|:------:|:------:|:------:|
|FFh      |B0h      |00h     |Block N°|N° Bytes|
|FFh      |B0h      |00h     |04h     |10h     |


Write MIFARE Classic Block #04 all 16 Bytes
|   CLA   |   INS   |   P1   |   P2   |   Le   |                   Data #16                     |
|:-------:|:-------:|:------:|:------:|:------:|:----------------------------------------------:|
|FFh      |D6h      |00h     |Block N°|N° Bytes|                    Data                        |
|FFh      |D6h      |00h     |04h     |10h     |00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0Fh|


Increment MIFARE Classic Value Block #05 (Increment Value Block with value of 100)
|   CLA   |   INS   |   P1   |   P2   |   Lc   | DataB1 | DataB2 | DataB3 | DataB4 | DataB5 | DataB6 |
|:-------:|:-------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|--------|
|FFh      |F0h      |00h     |Block N°|N° Bytes|Block N°|VP_OP   |VP_VALUE|VP_VALUE|VP_VALUE|VP_VALUE|
|FFh      |F0h      |00h     |05h     |06h     |01h     |C1h     |64h     |00h     |00h     |00h     |


Decrement MIFARE Classic Value Block #05 (Decrement Value Block with value of 1)
|   CLA   |   INS   |   P1   |   P2   |   Lc   | DataB1 | DataB2 | DataB3 | DataB4 | DataB5 | DataB5 |
|:-------:|:-------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|
|FFh      |F0h      |00h     |Block N°|N° Bytes|Block N°|VP_OP   |VP_VALUE|VP_VALUE|VP_VALUE|VP_VALUE|
|FFh      |F0h      |00h     |05h     |06h     |05h     |C0h     |01h     |00h     |00h     |00h     |


RESPONSES:
9000h -> OK
For error responses look up in https://www.eftlab.com/knowledge-base/complete-list-of-apdu-responses/

