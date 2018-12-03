/*
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as m from "mithril";
import * as Stream from "mithril/stream";
import {Notification, SystemNotifications} from "models/notifications/system_notifications";
import {SystemNotificationsWidget} from "views/components/notification_center/system_notifications_widget";

describe("SystemNotificationsWidget", () => {
  const simulateEvent = require("simulate-event");

  require("jasmine-jquery");
  require("jasmine-ajax");

  let $root: any, root: any;
  beforeEach(() => {
    //@ts-ignore
    [$root, root] = window.createDomElementForTest();
  });
  //@ts-ignore
  afterEach(window.destroyDomElementForTest);

  const systemNotifications = Stream(new SystemNotifications());

  beforeEach(() => {
    m.mount(root, {
      view() {
        return (<SystemNotificationsWidget systemNotifications={systemNotifications}/>);
      }
    });
    m.redraw();
  });

  beforeEach(() => {
    localStorage.clear();
    localStorage.setItem("system_notifications", JSON.stringify([]));
  });

  afterEach(() => {
    m.mount(root, null);
    m.redraw();
  });

  it("should not display notification bell when there are no notifications to display", () => {
    systemNotifications(SystemNotifications.fromJSON([]));
    m.redraw();
    expect($root.find(".notifications").get(0)).not.toBeInDOM();
  });

  it("should display notification bell and the notifications", () => {
    const notifications = [
      {
        id: "id1",
        message: "message 1.",
        read: false,
        type: "UpdateCheck",
        link: "link_1",
        linkText: "read more"
      },
      {
        id: "id2",
        message: "message 2.",
        read: false,
        type: "SomethingOfImportance",
        link: "link_2",
        linkText: "read more"
      }
    ] as Notification[];

    systemNotifications(SystemNotifications.fromJSON(notifications));
    m.redraw();
    const allNotifications = find("notification-item");
    expect(allNotifications.get(0)).toBeInDOM();
    expect(allNotifications.length).toBe(2);
    expect(allNotifications.eq(0)).toContainText("message 1. read more");
    expect(allNotifications.eq(0).find("a")).toContainText("read more");
    expect(allNotifications.eq(0).find(`[data-test-id='notification-item_close']`)).toContainText("X");
    expect(allNotifications.eq(1)).toContainText("message 2. read more");
    expect(allNotifications.eq(1).find("a")).toContainText("read more");
    expect(allNotifications.eq(1).find(`[data-test-id='notification-item_close']`)).toContainText("X");
  });

  it("should mark a notification as read and stop displaying it when user marks a notification as read", () => {
    const notifications = [
      {
        id: "id1",
        message: "message 1.",
        read: false,
        type: "UpdateCheck",
        link: "link_1",
        linkText: "read more"
      },
      {
        id: "id2",
        message: "message 2.",
        read: false,
        type: "SomethingOfImportance",
        link: "link_2",
        linkText: "read more"
      }
    ] as Notification[];

    systemNotifications(SystemNotifications.fromJSON(notifications));
    m.redraw();
    let allNotifications = find("notification-item");
    expect(allNotifications.length).toBe(2);
    simulateEvent.simulate(allNotifications.eq(0).find(`[data-test-id='notification-item_close']`).get(0), "click");
    m.redraw();

    allNotifications = find("notification-item");
    expect(allNotifications.length).toBe(1);
    expect(allNotifications.eq(0)).toContainText("message 2. read more");

    expect(systemNotifications().find((m: Notification) => {
      return m.id === "id1";
    })).toBeUndefined();

    const remainingNotification = systemNotifications().find((notification: Notification) => {
      return notification.id === "id2";
    }) as Notification;

    expect(remainingNotification).not.toBeUndefined();
    expect(remainingNotification.message).toBe("message 2.");
    expect(remainingNotification.read).toBe(false);
  });

  function find(id: string) {
    return $root.find(`[data-test-id='${id}']`);
  }
});
