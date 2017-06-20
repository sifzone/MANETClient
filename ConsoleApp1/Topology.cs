using System.Collections.Generic;
using System.Runtime.Serialization;

namespace ManetClient
{
    [DataContract]
    public class Topology
    {
        [DataMember]
        public string Type { get; set; }
        [DataMember]
        public int Sender { get; set; }
        [DataMember]
        public int Sequence { get; set; }
        [DataMember]
        public List<int> Neighbors { get; set; }
    }
}
