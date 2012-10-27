/*
 *
 * Copyright (C) 2007-2012 The kune development team (see CREDITS for details)
 * This file is part of kune.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cc.kune.core.server.rack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.inject.Singleton;

@Singleton
public class AdminThread extends Thread {

  private ServerSocket socket;

  public AdminThread() {
    setDaemon(true);
    setName("StopMonitor");
    try {
      socket = new ServerSocket(8079, 1, InetAddress.getByName("127.0.0.1"));
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    System.out.println("*** running jetty 'stop' thread");
    Socket accept;
    try {
      accept = socket.accept();
      final BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
      reader.readLine();
      System.out.println("*** stopping jetty embedded server");
      accept.close();
      socket.close();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
