using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Runtime.CompilerServices;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;


namespace ManetClient
{
    partial class Program
    {
        static void Main()
        {

            try
            {





                TcpClient client = new TcpClient(ConnectionDetails.IpAddress, ConnectionDetails.Port);
                ConnectionDetails.StreamReader = new StreamReader(client.GetStream());
                ConnectionDetails.StreamWriter = new StreamWriter(client.GetStream());
                string reply = ConnectionDetails.StreamReader.ReadLine();
                Console.WriteLine(reply);
                Tools.LogConsole(reply);
                ConnectionDetails.Id = Convert.ToInt16(FetchNumbersFromString(reply, ":"));

                Timers.NodesCheckTimer nodesCheckTimer=new Timers.NodesCheckTimer();
                Timers.HelloMessageTimer helloMessageTimer = new Timers.HelloMessageTimer();
                Timers.TopologyMessageTimer topologyMessageTimer = new Timers.TopologyMessageTimer();
               
                //ConnectionDetails.streamWriter.Flush();
                //ConnectionDetails.streamReader.Close();
                //ConnectionDetails.streamReader = null;
                //ConnectionDetails.streamWriter = null;

                //client.Close();

                Console.ReadLine();
            }
            catch (Exception ex)
            {

                Console.WriteLine(ex.Message);
                Console.ReadLine();
            }
        }

        public static string FetchNumbersFromString(string target, string dividerSymbol = "")
        {
            var ss = Regex.Split(target, @"[^0-9]+").Where(c => c.Trim() != "").ToList();
            string res = ss.Aggregate(string.Empty, (current, t) => current + (t + dividerSymbol));

            if (dividerSymbol != null && dividerSymbol.Trim().Length > 0)
                return (res.Length > 0) ? res.TrimEnd(Convert.ToChar(dividerSymbol)) : res;
            else
                return res;

        }


    }
}
