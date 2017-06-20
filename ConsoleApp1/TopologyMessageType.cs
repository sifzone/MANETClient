using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;

namespace ManetClient
{
    public class TopologyMessageType : IMessageType
    {
        public void SendMessage(int iD, TcpClient tcpClient, StreamWriter streamWriter)
        {
            IOperationType sendTcpMessage = new SendTcpMessage();
            Console.WriteLine("Topology message was sent");
            //Tools.LogConsole("Sent hello message");

            Topology topology = new Topology()
            {
                Type = "topology",
                Sender = ConnectionDetails.Id,
                Sequence = ++ConnectionDetails.Sequence,
                Neighbors = new List<int>() { 1, 2, 3 },
            };

            string json = Tools.SerializeToJson(topology);

            sendTcpMessage.Execute(json, tcpClient,streamWriter);
        }
    }
}