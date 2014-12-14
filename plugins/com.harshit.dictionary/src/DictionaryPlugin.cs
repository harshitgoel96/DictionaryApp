using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;
using com.ionicframework.dictionaryapp240429.DictionaryService;
namespace Cordova.Extension.Commands
{
    class dictionaryPlugin:BaseCommand
    {
        public void define(string options)
        {
            System.Diagnostics.Debug.WriteLine("We are here");
            string word = JsonHelper.Deserialize<string[]>(options)[0];
            DictServiceSoapClient serviceClient = new DictServiceSoapClient();
            serviceClient.DefineCompleted += new EventHandler<DefineCompletedEventArgs>(getMeanings);
            serviceClient.DefineAsync(word);
        }
        void getMeanings(object sender,DefineCompletedEventArgs e){
            System.Diagnostics.Debug.WriteLine(e.Result.ToString());
        }
    }


}
