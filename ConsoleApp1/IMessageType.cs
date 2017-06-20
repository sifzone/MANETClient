using System.IO;
using System.Net.Sockets;

namespace ManetClient
{
    public interface IMessageType
    {
        void SendMessage(int iD, TcpClient tcpClient, StreamWriter streamWriter);
    }
}