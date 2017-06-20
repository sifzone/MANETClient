using System.Runtime.Serialization;

namespace ManetClient
{
    [DataContract]
    public class HelloMessage
    {
        [DataMember]
        public string type { get; set; }
        [DataMember]
        public int sender { get; set; }
    }
}