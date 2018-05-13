package com.smackdemo;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChatUtil {

    private static ChatUtil chatUtil;
    private String TAG = "ChatUtil";
    private XMPPConnection xmppConnection;
    private ChatManager chatManager;
    private LoginListener loginListener;
    private List<IncomingChatMessageListener> incomingChatMessageListeners;
    private AbstractXMPPConnection abstractXMPPConnection;

    private ChatUtil() {
        incomingChatMessageListeners = new ArrayList<>();
    }

    public static ChatUtil getInstance() {
        if (chatUtil == null) {
            chatUtil = new ChatUtil();
        }
        return chatUtil;
    }

    public void disConnect() {
        if (abstractXMPPConnection != null) {
            abstractXMPPConnection.disconnect();
        }
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public ChatUtil connectServer(final String name, final String psw) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress address = InetAddress.getByName("47.94.130.167");
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(name, psw)
                            .setXmppDomain("ykco")
                            .setHostAddress(address)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .setPort(5222)
                            .build();
                    abstractXMPPConnection = new XMPPTCPConnection(config);
                    abstractXMPPConnection.addConnectionListener(new ConnectionManager());
                    abstractXMPPConnection.connect().login();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return chatUtil;
    }


    public void addChatListener(IncomingChatMessageListener listener) {
        incomingChatMessageListeners.add(listener);
    }

    public void sendMsg(String msg, String friendId) {
        if (chatManager == null) {
            Log.e(TAG, "sendMsg: 连接服务器失败！");
            return;
        }
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(friendId);
            Chat chat = chatManager.chatWith(jid);
            chat.send(msg);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addFriend(String friendId, String nickName) {
        //添加好友
        Log.e(TAG, "addFriend: " + friendId);
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(friendId);
            Roster roster = Roster.getInstanceFor(xmppConnection);
            roster.createEntry(jid, nickName, new String[]{"Friends"});
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }


    }

    private void addFriendListener() {
        //条件过滤器
        AndFilter filter = new AndFilter(new StanzaTypeFilter(Presence.class));
        //添加监听
        xmppConnection.addAsyncStanzaListener(packetListener, filter);
    }

    public List<FriendInfoBean> getFriendList() {
        if (xmppConnection == null) {
            Log.e(TAG, "getFriendList: 内容为空");
            return null;
        }
        Roster instanceFor = Roster.getInstanceFor(xmppConnection);
        Set<RosterEntry> entries = instanceFor.getEntries();
        Log.e(TAG, "getFriendList: entries size " + entries.size());
        List<FriendInfoBean> friendList = new ArrayList<>();

        for (RosterEntry entry : entries) {
            entry.getType();
            String name = entry.getName();//昵称e
            entry.getGroups();//好友所在的组
            entry.getJid().getDomain();//好友域名
            entry.getJid().getLocalpartOrNull();//好友名称
            String id = entry.getJid().toString();
            Log.e(TAG, "getFriendList: id: " + id);
            FriendInfoBean friend = new FriendInfoBean(id, id);
            friendList.add(friend);
        }
        return friendList;
    }


    private StanzaListener packetListener = new StanzaListener() {
        @Override
        public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {

            if (packet instanceof Presence) {
                Presence presence = (Presence) packet;
                Jid fromId = presence.getFrom();
                String from = fromId.toString();
                if (presence.getType().equals(Presence.Type.subscribe)) {
                    Log.e(TAG, "请求添加好友" + from);
                    Presence pres = new Presence(Presence.Type.subscribed);//默认同意所有好友请求
                    pres.setTo(fromId);
                    try {
                        xmppConnection.sendStanza(pres);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                } else if (presence.getType().equals(Presence.Type.subscribed)) {//对方同意订阅
                    Log.e(TAG, "同意订阅" + from);
                } else if (presence.getType().equals(Presence.Type.unsubscribe)) {//取消订阅
                    Log.e(TAG, "取消订阅" + from);
                } else if (presence.getType().equals(Presence.Type.unsubscribed)) {//拒绝订阅
                    Log.e(TAG, "拒绝订阅" + from);
                } else if (presence.getType().equals(Presence.Type.unavailable)) {//离线
                    Log.e(TAG, "离线" + from);
                } else if (presence.getType().equals(Presence.Type.available)) {//上线
                    Log.e(TAG, "上线" + from);
                }
            }
        }

    };

    public interface LoginListener {

        void onSuc();

        void onError(String msg);
    }

    private class ConnectionManager implements ConnectionListener {
        @Override
        public void connected(XMPPConnection connection) {

        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            xmppConnection = connection;
            addFriendListener();
            Log.e(TAG, "authenticated: ");
            if (loginListener != null) {
                loginListener.onSuc();
            }
            try {
                Presence presence = new Presence(Presence.Type.available);
                presence.setStatus("OnLine");
                connection.sendStanza(presence);
                chatManager = ChatManager.getInstanceFor(connection);
                chatManager.addIncomingListener(new ChatMsgManager());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void connectionClosed() {

        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.e(TAG, "connectionClosedOnError: ");
            if (loginListener != null)
                loginListener.onError(e.getMessage());
        }

        @Override
        public void reconnectionSuccessful() {

        }

        @Override
        public void reconnectingIn(int seconds) {

        }

        @Override
        public void reconnectionFailed(Exception e) {

        }
    }

    private class ChatMsgManager implements IncomingChatMessageListener {
        @Override
        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
            if (incomingChatMessageListeners.size() == 0) {
                Log.e(TAG, "newIncomingMessage: " + "没有注册任何消息接收者");
                return;
            }
            for (IncomingChatMessageListener listener : incomingChatMessageListeners) {
                listener.newIncomingMessage(from, message, chat);
            }
        }
    }
}
