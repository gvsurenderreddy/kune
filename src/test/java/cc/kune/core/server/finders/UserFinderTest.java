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
package cc.kune.core.server.finders;

import static junit.framework.Assert.*;

import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import cc.kune.core.server.PersistencePreLoadedDataTest;
import cc.kune.domain.User;
import cc.kune.domain.finders.UserFinder;

import com.google.inject.Inject;

public class UserFinderTest extends PersistencePreLoadedDataTest {
  @Inject
  UserFinder finder;

  @Test
  public void findAll() {
    final List<User> all = finder.getAll();
    assertEquals(3, all.size());
  }

  @Test
  public void findByEmail() {
    final User user = finder.findByEmail("one@here.com");
    assertNotNull(user);
  }

  @Before
  public void initData() {
    persist(new User("shortname1", "the name1", "one@here.com", "diggest".getBytes(), "salt".getBytes(),
        english, gb, TimeZone.getDefault()));
    persist(new User("shortname2", "the name2", "two@here.com", "somediggest".getBytes(),
        "salt".getBytes(), english, gb, TimeZone.getDefault()));
  }

}
