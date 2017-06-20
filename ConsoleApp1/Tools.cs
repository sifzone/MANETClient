using System;
using System.IO;
using System.Web.Script.Serialization;

namespace ManetClient
{
    class Tools
    {

        public static void LogConsole(string message)
        {
            FileStream fileStream;
            if (!File.Exists(@"log.txt"))

                fileStream = new FileStream(@"log.txt", FileMode.OpenOrCreate, FileAccess.ReadWrite,
                    FileShare.None);
            else

                fileStream = new FileStream(@"log.txt", FileMode.Append, FileAccess.Write,
                    FileShare.None);

            using (StreamWriter sw = new StreamWriter(fileStream))
            {
                sw.WriteLine(message);
                sw.Close();
            }
            fileStream.Close();
        }

        public static string SerializeToJson(Object obj)
        {
            try
            {
                return new JavaScriptSerializer().Serialize(obj);
            }
            catch (Exception)
            {

                return string.Empty;
            }
        }

        public static object DeSerializeJson(string json)
        {
            try
            {
                JavaScriptSerializer jsonSerializer = new JavaScriptSerializer();
                var obj = jsonSerializer.DeserializeObject(json);
                return obj;
            }
            catch (Exception)
            {
                return null;
              
            }
        }
    }
}
