package com.Phouse;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            // get a port
            ServerSocket serverSocket = new ServerSocket(0);

            // display connection information
            System.out.println("IP Address: " + getCurrentIp());
            System.out.println("Port Number: " + serverSocket.getLocalPort());

            Robot robot = new Robot();
            Socket socket = serverSocket.accept();

            // listen for command
            while (true)
            {
                // get new connection if needed
                if(socket.isClosed())
                {
                    socket = serverSocket.accept();
                }

                DataInputStream input = new DataInputStream(socket.getInputStream());

                String command = "";
                try
                {
                     command = input.readUTF();
                }
                catch(EOFException e)
                {
                    socket = serverSocket.accept();
                    continue;
                }

                if (!command.isEmpty())
                {
                    // triage command
                    if (command.contains("move"))
                    {
                        // move cursor relative to current position
                        String[] parts = command.split(" ");
                        if (parts.length == 3)
                        {
                            PointerInfo a = MouseInfo.getPointerInfo();
                            Point b = a.getLocation();
                            int x = (int) b.getX() + Integer.parseInt(parts[1]);
                            int y = (int) b.getY() + Integer.parseInt(parts[2]);
                            robot.mouseMove(x, y);
                        }
                    }
                    else if (command.contains("left"))
                    {
                        // left click
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    }
                    else if (command.contains("right"))
                    {
                        // right click
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                    }
                    else if (command.contains("release"))
                    {
                        // release left click
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    }
                    else if (command.contains("test"))
                    {
                        // send OK
                        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                        output.writeUTF("ok");
                        output.flush();
                    }
                    else if (command.contains("scroll"))
                    {
                        // scroll
                        String[] parts = command.split(" ");
                        if (parts.length == 2)
                        {
                            int notches = Integer.parseInt(parts[1]);
                            robot.mouseWheel(notches);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String getCurrentIp()
    {
        try
        {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while(addresses.hasMoreElements())
                {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLinkLocalAddress() && !address.isLoopbackAddress() && address instanceof Inet4Address)
                    {
                        return address.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
