# AppWatch
### National College of Ireland
Higher Diploma in Science in Computing (Cybersecurity) Semester 3 Project.
The purpose of this utility is to provide users of Windows 7 or higher with a means to scan their computer for installed applications, and then scan online exploit databases for known vulnerabilties for those applications, using the free Shodan API.  The appprovides a report with descriptions of the vulnerabilities found and links to the Common Vulnerabilities and Exposures page for each.

## Getting Started

### Prerequisites

Microsft Windows 7 (or later)

Java JRE 1.7 (or later) https://java.com/en/

### Installing

#### Application Install
Download and run [AppWatch.exe](https://github.com/harropda/AppWatch/blob/master/AppWatch/AppWatch.exe)

#### Development Download
Clone the Appwatch/src/appwatch directory.
Add this directory as a new Ppoject in your IDE

### Run
When run for the first time, AppWatch creates a new directory in the Users home directory in which it stores reports and the contrl file (hashlist.xml).  The new directory is

* C:\Users\%USERNAME%\AppWatch

If you delete this directory, AppWatch will behave as if it has been newly installed.

## Built With

* [Netbeans](https://netbeans.org/) - Java Development IDE

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

* **David Harrop** - *All work* - [harropda](https://github.com/harropda)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* D. Weymouth - EncryptionManager.java
* Any and all contributors at stackoverflow -we'd be nowhere without our dev community.
