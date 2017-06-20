using System.IO;
using System.Net.Sockets;

namespace ManetClient
{
    public interface IOperationType
    {
        void Execute(string message, TcpClient tcpClient,StreamWriter streamWriter);
    }
}
