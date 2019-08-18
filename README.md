
<p align="center"><img src="/IMAGES/Logo.png" title="RoboTour" width="245" height="170" align="center" /></p>


<h1  align="center" style="text-align: center;"><span  align="center" style="color: #ff0000;"><strong><span align="center" style="color: #000000;"> SDP - GROUP 18 - 2017/18
</span> </strong></span></h1>
<p style="text-align: center;">&nbsp;</p>


RoboTour is a robotic tour guide that assists people in environments such as museums or art galleries. The system comprises an autonomous robotic guide, a purpose-built Android application, and a web server mediating the communication between the two. Up to two Android devices can control RoboTour and followed by many more. The app allows users to interact with RoboTour intuitively in multiple languages. RoboTour has been designed for minimal maintenance; once the initial setup has been performed. 

[RoboTour Promotional Video](https://www.youtube.com/watch?v=iU0O0e72A&feature=youtu.be)

[![Directed by Finn, Actors: Alice, Mahbub and Michal](http://img.youtube.com/vi/is1U0O0e72A/0.jpg)](https://youtu.be/hma03HVH12Y "Directed by Finn, Actors: Alice, Mahbub and Michal")

### [SDP - 2017/2018 ](http://www.drps.ed.ac.uk/17-18/dpt/cxinfr09032.htm)
The System Design Project is intended to give students practical experience of 

(a) building a large scale system 
<br>
<br>
(b) working as members of a team. 

The [Systems Design Project ](http://www.drps.ed.ac.uk/17-18/dpt/cxinfr09032.htm) is a University of Edinburgh, Semester 2 module, SDP is a group project involving the construction of an item of significant complexity under conditions designed to give insights into industrial teamwork.
<br>
<br>
It is a 20 credit course with design, construction and assessment through the semester, ending in a demonstration day with industry visitors.
<br>
<br>
More detail about the course and how it will run this year are in these slides from the introductory lecture held in semester 1 Professional Issues.
<br>
<br>
The Project
<br>
<br>
SDP 2017/2018's task is to use Lego and an Arduino to design an assistive robotic device, with an appropriate software interface.
<br>
<br>
Flexibility was given to set your own goals for this task, but here is an example of what you could attempt: a person indicates an object on the floor with a laser pointer, and the robot picks it up and returns it to the person.
<br>
<br>
Groups
<br>
<br>
The class is assigned to groups of 7 or 8, each responsible for the development of a single robot. Assessment involves group marks for the product (performance and documentation of the robot systems).
<br>
<br>
Each group had an assigned mentor, with whom they meet around once a week, and who offers advice and monitors progress, but - importantly - does not lead or manage the group. Advice on how the group should organise themselves for good project management will be provided. Problems within the group should first be brought to the attention of your mentor; if you have an issue with your mentor, you should bring this to the attention of the SDP TA (see below).

The project involves applying and combining material from several courses to complete a complex design and implementation task. 
At the end, of course, each group demonstrates its implemented system and gives a formal presentation to an audience of the students, supervisors, and visitors from industry (E.g. Google, Amazon, KAL, Robotical).

[DRPS SDP 2017/2018](http://www.drps.ed.ac.uk/17-18/dpt/cxinfr09032.htm)


### [Award - Technical Innovation Prize](https://www.ed.ac.uk/informatics/news-events/stories/2018/students-showcase-projects-to-industry-experts) 
The project was developed for System Design Project at the University of Edinburgh.
All 20 projects were assessed by external judges from industry (e.g. Google, Amazon, Accenture, KAL, Sky).
Team RoboTour was awarded the [Technical Innovation Prize](https://www.ed.ac.uk/informatics/news-events/stories/2018/students-showcase-projects-to-industry-experts)

### [Specifications](https://github.com/mahbubiftekhar/RoboTour/blob/master/DOCUMENTS/Submissions/ProjectPlan.pdf) 
RoboTour provides four key features to enhance the user’s experience

* **Multi-language support in Human-Robot Interaction via speech and app** 
* **Guides visitors to a specific art piece and points it out to the user**
* **Plays audio description of art pieces in the language the user selected** 
* **Provides recommendations and optimal route planning routes**

### [Target Users](https://github.com/mahbubiftekhar/RoboTour/blob/master/DOCUMENTS/Submissions/ProjectPlan.pdf) 
RoboTour assists people who have one of the following problems:
<br>
<br>
•    They’re a museum visitor who needs directional assistance
<br>
•    Or they’re a visitor who cannot read the displays in the museum whether this is because they cannot read the language or because they have problems with their vision.
<br>
<br>
RoboTour robot can interact with visitors and guide them to the piece of art they are looking for by moving with the user through the museum and pointing out the art piece upon arrival.

### [Software Structure](https://github.com/mahbubiftekhar/RoboTour/blob/master/DOCUMENTS/Submissions/TechnicalReport.pdf) 
There are three main components to RoboTour: 
* Android App - Responsible for allowing the user to select paintings they wish to go to and send commands to the robot.
* Server: All Android devices communicate to the robot via the server. The server is responsible for mediating and storing commands between all Android devices and the robot. The purpose of having the server is to allow multiple android devices to communicate with the robot. 
* Robot: Oversees path planning and navigation around the museum
<img src="/IMAGES/table.png" title="RoboTour" width="600" height="120" />

### [The App](https://mahbubiftekhar.co.uk/download.php) 
The app is backwards compatible with older versions of Android; the app will work with Android SDK version 17 onwards (users also require 20mb free space and an internet connection). The app was developed in Android Studio 3.1 using Kotlin. 

<img src="/IMAGES/s1.png" title="RoboTour" width="200" height="390" /> <img src="/IMAGES/s2.png" title="RoboTour" width="200" height="390" /> <img src="/IMAGES/s3.png" title="RoboTour" width="200" height="390" />   <img src="/IMAGES/s4.png" title="RoboTour" width="200" height="390" />
<br>
<br>
<br>

<img src="/IMAGES/s5.png" title="RoboTour" width="200" height="390" /><img src="/IMAGES/s6.png" title="RoboTour" width="200" height="390" /><img src="/IMAGES/s7.png" title="RoboTour" width="200" height="390" /><img src="/IMAGES/s8.png" title="RoboTour" width="200" height="390" />

### [The Robot](https://github.com/mahbubiftekhar/RoboTour/blob/master/DOCUMENTS/Submissions/UserGuide.pdf) 
The robot is a differential drive platform, i.e. the movement is achieved with two motorised drive wheels. Varying the rotational speed of the wheels independently, allowed us to introduce rotation of the chassis in addition to the linear translation. Additionally, two rear wheels are added for stability and weight support. They were designed with the aim of minimising the friction and disturbance of the robot control.



 <img src="/IMAGES/DSC_0943.JPG" title="RoboTour: Start Position" width="300" height="190" />
 <br>
 <img src="/IMAGES/Brochure3Rounded.png" title="RoboTour: Mona Lisa" width="300" height="190" />
 <br>
 <img src="/IMAGES/robot.png" title="RoboTour: Labelled Diagram" width="300" height="190" />

<br>
 

### [Installing The App](https://github.com/mahbubiftekhar/RoboTour/blob/master/DOCUMENTS/Submissions/UserGuide.pdf)
<br>
To install the app on an Android device, installation, you are required to enable [Unknown Sources](https://www.androidcentral.com/unknown-sources).
<br>
This feature is turned off by default on stock Android, and can be turned on by following these steps: 

Device Settings ​-> ​Advanced Settings ​-> ​Security ​->​ Enable Unknown Sources   

Once the app is downloaded, go to the Downloads folder on your phone and click on the apk or select it from the notifications bar. Follow the installation instructions. Once installed the app will be in your App drawer under RoboTour. Tap the app to open it. 


## Group Members

* **[Mahbub Iftekhar](https://www.mahbubiftekhar.co.uk/)** - *Team Manager & Android Developer*
* **[David Speers](https://github.com/davidspeers)** - *Android Developer & UI Designer* 
* **[Michal Dauenhauer](https://github.com/michuszkud)** - *Embedded Developer & Custom Sensor Guru*
* **[Alice Wu](https://github.com/AliceWoooo)** -  *Robotics Software Developer*
* **[Devidas Lavrik](https://github.com/DLavrik)** - *Lego Builder & PID Expert* 
* **[Finn Zhan Chen](http://finnzhanchen.com/)** - *Business Analyst*
* **[Mariyana Cholakova](https://github.com/chMariyana)** - *Designer & Admin* 

## Contact us
You are welcome to visit out [Facebook page](https://www.facebook.com/RoboTour/) or send us an e-mail on robotour.sdp@gmail.com 


## References

* GitHub. (2018). Kotlin/anko. [online] Available at: https://github.com/Kotlin/anko [Accessed 9 Apr. 2018].

* The Verge. (2018). 99.6 percent of new smartphones run Android or iOS. [online] Available at: https://www.theverge.com/2017/2/16/14634656/android-ios-market-share-blackberry-2016 [Accessed 9 Apr. 2018].

* Google Cloud Speech API. (2018). Cloud Speech API Documentation  |  Google Cloud Speech API  |  Google Cloud. [online] Available at: https://cloud.google.com/speech/docs/ [Accessed 9 Apr. 2018].

* Cloud Text-to-Speech API. (2018). Cloud Text-to-Speech API Basics  |  Cloud Text-to-Speech API  |  Google Cloud. [online] Available at: https://cloud.google.com/text-to-speech/docs/basics [Accessed 9 Apr. 2018].

* Skiena, S. (1990). Dijkstra’s algorithm. Implementing Discrete Mathematics: Combinatorics and Graph Theory with Mathematica, Reading, MA: Addison-Wesley, 225-227.

* Gilles-bertrand.com. (2018). Dijkstra algorithm: How to implement it with Python (solved with all explanations)? | Gilles' Blog. [online] Available at: http://www.gilles-bertrand.com/2014/03/dijkstra-algorithm-python-example-source-code-shortest-path.html [Accessed 11 Apr. 2018].


## MIT License

Copyright (c) 2018 RoboTour Authors 

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
