using System;
using System.IO;
using System.Net.Sockets;

namespace ManetClient
{
    public class HelloMessageType : IMessageType
    {

        public void SendMessage(int iD, TcpClient tcpClient, StreamWriter streamWriter)
        {
            IOperationType sendTcpMessage = new SendTcpMessage();
            Console.WriteLine("Hello message was sent");
            //Tools.LogConsole("Sent hello message");
            sendTcpMessage.Execute("{" + $"\"type\": \"hello\", \"sender\": {iD}" + "}", tcpClient,streamWriter);
        }
    }
}
