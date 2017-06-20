using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;

namespace ManetClient
{
    public static class ConnectionDetails
    {
        public static int Id=0;
        public const string IpAddress = "hive-01.informatik.uni-bonn.de";
        public const int Port = 8888;
        public static TcpClient TcpClient;
        public static StreamReader StreamReader;
        public static StreamWriter StreamWriter;
        public static Int16 Sequence = 0;
        public static int CheckSecond = 0;
        public static List<int> Senders { get; set; }=new List<int>();
        public static List<int> CurrentNodesList { get; set; }=new List<int>();
        public static List<int> TemporaryNodesList { get; set; } = new List<int>();
        public static List<Topology> TopologyList { get; set; } = new List<Topology>();
    }
}