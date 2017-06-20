using System;
using System.IO;
using System.Net.Sockets;
using System.Text;

namespace ManetClient
{
    public class SendTcpMessage : IOperationType
    {
        public void Execute(string message, TcpClient tcpClient, StreamWriter streamWriter)
        {
            char[] bytesToSend = Encoding.ASCII.GetChars(Encoding.ASCII.GetBytes(message));
            streamWriter.Write(bytesToSend, 0, bytesToSend.Length);
            //streamWriter.Write(message);
        }
    }
}
