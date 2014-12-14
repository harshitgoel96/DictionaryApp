using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;
using Newtonsoft.Json;
using com.ionicframework.dictionaryapp240429.DictionaryService;
using System.Net;
namespace Cordova.Extension.Commands
{
    class dictionaryPlugin : BaseCommand
    {
        public void define(string options)
        {
            System.Diagnostics.Debug.WriteLine("We are here");
            string word = JsonHelper.Deserialize<string[]>(options)[0];
            DictServiceSoapClient serviceClient = new DictServiceSoapClient();
                serviceClient.DefineCompleted += new EventHandler<DefineCompletedEventArgs>(getMeanings);
                serviceClient.DefineAsync(word);
            
        }
        void getMeanings(object sender, DefineCompletedEventArgs e)
        {
            //System.Diagnostics.Debug.WriteLine(e.Result.ToString());

            try
            {
                List<Definition> defList = e.Result.Definitions.ToList();
                List<string> definitions = new List<string>();
                
                //string jsonArray = JsonConvert.SerializeObject(defList, Formatting.Indented);
                Dictionary<string, List<Definition>> obj = new Dictionary<string, List<Definition>>();
                obj.Add("Definitions", defList);
                Dictionary<string, Dictionary<string, List<Definition>>> jsonDic = new Dictionary<string, Dictionary<string, List<Definition>>>();
                jsonDic.Add("DATA", obj);
                string jsonObj = JsonConvert.SerializeObject(jsonDic, Formatting.Indented);
                System.Diagnostics.Debug.WriteLine(jsonObj);
                PluginResult result = new PluginResult(PluginResult.Status.OK,jsonObj);
                result.KeepCallback=false;
                DispatchCommandResult(result);
            }
            catch (Exception ex)
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "Service Error: Did not get proper response from the server, Please check network connectivity"));
                System.Diagnostics.Debug.WriteLine(ex.ToString());
            }
        }
    }


}
