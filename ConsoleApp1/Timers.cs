using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;
using System.Timers;

namespace ManetClient
{
    partial class Program
    {
        public class Timers
        {

            public class HelloMessageTimer
            {
                readonly IMessageType _helloMessage = new HelloMessageType();
                private static Timer _helloMessageTimer;

                public HelloMessageTimer()
                {
                    _helloMessageTimer = new Timer
                    {
                        Interval = 2000,
                        Enabled = true
                    };
                    _helloMessageTimer.Elapsed += async delegate(Object source, System.Timers.ElapsedEventArgs e)
                    {
                        await Task.Run(() =>
                            {


                               _helloMessage.SendMessage(ConnectionDetails.Id,
                                    ConnectionDetails.TcpClient,
                                    ConnectionDetails.StreamWriter);
                               
                            }
                        );

                        //Tools.LogConsole(reply);


                    };
                }



                ~HelloMessageTimer()
                {
                    _helloMessageTimer.Dispose();
                }

                public void StopTimer()
                {
                    _helloMessageTimer.Stop();
                }

                public void RestartTimer()
                {
                    _helloMessageTimer.Start();
                }
            }

            public class TopologyMessageTimer
            {
                readonly IMessageType _topologyMessageType = new TopologyMessageType();

                public TopologyMessageTimer()
                {
                    var topologyMessageTimer = new Timer
                    {
                        Interval = 5000,
                        Enabled = true

                    };
                    topologyMessageTimer.Elapsed += async delegate(Object source, System.Timers.ElapsedEventArgs e)
                    {
                        await Task.Run(() =>
                            {
                                _topologyMessageType.SendMessage(ConnectionDetails.Id,ConnectionDetails.TcpClient,ConnectionDetails.StreamWriter);
                                
                               //Tools.LogConsole(reply);
                            }
                        );
                    };
                }
            }

            public class NodesCheckTimer
            {

                public NodesCheckTimer()
                {
                    var checkTimer = new Timer
                    {
                        Interval = 1000,
                        Enabled = true

                    };
                    checkTimer.Elapsed += async delegate (Object source, System.Timers.ElapsedEventArgs e)
                    {
                        await Task.Run(() =>
                            {

                                ConnectionDetails.CheckSecond++;

                                var readLine = ConnectionDetails.StreamReader.ReadLine();
                                if (!string.IsNullOrEmpty(readLine))
                                {
                                    Dictionary<string, object> reply =
                                        (Dictionary<string, object>) Tools.DeSerializeJson(readLine);


                                    if (reply?.Count(x => x.Key == "type" && (string) x.Value == "hello") > 0)
                                    {

                                        object value = reply.Where(x => x.Key == "sender")
                                            .Select(x => x.Value)
                                            .SingleOrDefault();
                                        if (value != null)
                                        {
                                            ConnectionDetails.TemporaryNodesList.Add((int) value);
                                            if (ConnectionDetails.CurrentNodesList.Count(a => a == (int) value) == 0)
                                                ConnectionDetails.CurrentNodesList.Add((int) value);
                                        }
                                    }
                                    if (reply?.Count(x => x.Key == "type" && (string)x.Value == "topology") > 0)
                                    {

                                        object value = reply.Where(x => x.Key == "sender").Select(x => x.Value).SingleOrDefault();
                                        if (value != null)
                                        {
                                            if (ConnectionDetails.TopologyList.Count(a => a.Sender == (int)value) == 0)
                                                ConnectionDetails.TopologyList.Add(new Topology
                                                {
                                                    Type = "topology",
                                                    Sender = (int)value,
                                                    Sequence = reply.Where(x => x.Key == "sequence").Select(x => (int)x.Value).SingleOrDefault(),
                                                    Neighbors = new List<int>(5)
                                                });
                                        }
                                    }

                                }

                                if (ConnectionDetails.CheckSecond >= 20)
                                {
                                    ConnectionDetails.CheckSecond = 0;
                                    CleanUpNodeList();
                                    
                                }
                                if (!string.IsNullOrEmpty(readLine))
                                    Console.WriteLine($"Reply from checker timer:" + readLine);

                                //Tools.LogConsole(reply);
                            }
                        );
                    };
                }

                private void CleanUpNodeList()
                {

                    HashSet<int> nodes = new HashSet<int>(ConnectionDetails.TemporaryNodesList.Select(x => x));


                    ConnectionDetails.CurrentNodesList.RemoveAll(x => !nodes.Contains(x));

                    ConnectionDetails.TemporaryNodesList.Clear();
                }

            }
        }
    }
}
