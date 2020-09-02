# MifareClassic

MifareClassic is a set of Java code examples on how to use the javax.smartcardio API with APDU commands and response pairs.
The APDU is defined by the standard ISO/IEC 7816 part 4.

## Installation 

There is no complete package for installation, use the java docs as examples as fit. In order to run them, the recommendation is to use
an IDE like ApacheNetbeans, Eclipse or use VisualStudio Code

```bash
install ApacheNetbeans
install Eclipse
install VisualStudio Code
```

## Usage

```java
import javax.smartcardio*;

TerminalFactory factory = TerminalFactory.getDefault(); # returns the default TerminalFactory instance
CardTerminal terminals = factory.terminals().list();    # returns a new CardTerminals object encapsulating the terminals supported by this factory
Card card = terminal.connect("T=1");                    # constructs a new Card object (the protocol in use for this types of cards, for example "T=0" or "T=1")
CardChannel channel = card.getBasicChannel();           # returns the CardChannel for the basic logical channel
String ATR = card.getATR();                             # returns the ATR of this card
CommandAPDU readUID = new CommandAPDU(byte[] apdu);     # constructs a CommandAPDU from a byte array containing the complete APDU contents (header and body)
ResponseAPDU r = channel.transmit(byte[] apdu);         # constructs a ResponseAPDU from a byte array containing the complete APDU contents (conditional body and trailed)

```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[GNU](https://www.gnu.org/licenses/why-not-lgpl.html/)
