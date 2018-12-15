package com.Phouse;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            // get a port
            ServerSocket serverSocket = new ServerSocket(0);

            // get external ip
            URL checkip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(checkip.openStream()));
            String ip = in.readLine();

            // display connection information
            System.out.println("IP Address: " + ip);
            System.out.println("Port Number: " + serverSocket.getLocalPort());

            Robot robot = new Robot();

            // listen for command
            while (true)
            {
                Socket socket = serverSocket.accept();
                InputStream input = socket.getInputStream();

                String command = new String(input.readAllBytes());

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
                        robot.keyRelease(InputEvent.BUTTON3_DOWN_MASK);
                    }
                    else if (command.contains("release"))
                    {
                        // release left click
                        robot.keyRelease(InputEvent.BUTTON1_DOWN_MASK);
                    }
                    else if (command.contains("test"))
                    {
                        // send OK
                        socket.getOutputStream().write("OK".getBytes());
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

                socket.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
