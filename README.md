# DistributedTicketSystem
A high-performance, distributed ticket sale application
# Description
#1. How to run the project?
Cd to the path of build.sbt
sbt and run
There are two main classes:
1. buyticketclient.Client        ---- client
2. ticket.TicketApplication    ----- server
Firstly, we run server and then open another terminal to run the client. Try to run the client 6 times until the tickets are out of stock. 

#2. How the system works?
First of all, master will build several actor of kiosks. And the kiosks will initialize themselves. Then, master will get the ACK and make the ring to connect one kiosk with another kiosk. After that, it will start to loop in the current kiosk. 
In the loop of current kiosk, keep ask for tickets until there is zero ticket remaining in the kiosk. Then the current kiosk will ask more ticket from other kiosk. Finally, the system will run out of tickets.   
#3.The benefit in my approach 
The approach:
I use the approach to make a loop. So the tickets are easily handled and understood.  Also, messages are transmitted in one way to avoid the conflict. 
#4. For synchronization:
I choose to set up each kiosk as an individual actor. They will communicate with each other by messages but not interrupt each other.  
