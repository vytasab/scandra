package lt.node.scandra.pirkimai

// https://github.com/hyone/android-listview-scala  <-- some useful info

import android.app.ListActivity
import android.widget.{ArrayAdapter, AdapterView, ListView, Toast, TextView}
import android.widget.AdapterView._
import android.os.{Environment, Bundle}
import lt.node.scandra.pirkimai.util.FileUtil
//import android.content.ContentProvider._
import android.content.res.Resources
import java.io.{InputStream, File}
import xml.{XML, NodeSeq}

//OnItemClickListener._

import android.view.View

object PurchaseLI {
  private[pirkimai] final val COUNTRIES: Array[String] = Array[String]("Afghanistan",
    "Albania", "Algeria",
    "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica",
    "Antigua and Barbuda", "Argentina", "Armenia",
    "Aruba", "Australia", "Austria", "Azerbaijan", "Bahrain", "Bangladesh",
    "Barbados", "Belarus", "Haiti", "Honduras",
    "Hong Kong", "Hungary", "Iceland", "India",
    "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy",
    "Jamaica", "Japan", "Jordan", "Kazakhstan",
    "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia",
    "Lebanon", "Lesotho", "Liberia",
    "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
    "Macau", "Madagascar", "Malawi", "Malaysia",
    "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique",
    "Mauritania", "Mauritius", "Tuvalu",
    "Virgin Islands", "Uganda", "Ukraine", "United Arab Emirates",
    "United Kingdom", "United States",
    "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela",
    "Vietnam", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe")
}

class PurchaseLI extends ListActivity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    import PurchaseLI._

    super.onCreate(savedInstanceState)

    val resources: Resources = this.getResources
    val srcStream: InputStream = resources openRawResource R.raw.orderxml
    val srcString = scala.io.Source.fromInputStream(srcStream).getLines().mkString("\n")
    //http://stackoverflow.com/questions/5221524/idiomatic-way-to-convert-an-inputstream-to-a-string-in-scala
    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName());
    val dstFile = new File(dir, "orderxml.txt")
    FileUtil.writeStringAsFile(srcString, dstFile) /*: Boolean*/

    val orderFile = new File(dir, "orderxml.txt")
    //val order: Array[String] = FileUtil.readFileAsStringArray(orderFile)
    val orderString = FileUtil.readFileAsString(orderFile)
    setListAdapter(new ArrayAdapter[String](this, R.layout.purchase_list_item, orderItems(orderString).toArray))

    /*val countries = getResources().getStringArray(R.array.countries_array)
    // tinka abu variantai
    setListAdapter(new ArrayAdapter[String](this, R.layout.purchase_list_item, COUNTRIES))
    // setListAdapter(new ArrayAdapter[String](this, R.layout.purchase_list_item, countries))*/

    var lv: ListView = this.getListView

    lv.setTextFilterEnabled(true)

    lv.setOnItemClickListener(new OnItemClickListener() {
      override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
        Toast.makeText(
          getApplicationContext(),
          view.asInstanceOf[TextView].getText(),
          Toast.LENGTH_LONG
        ).show()
      }
    })

  }


  def orderItems(xmlStr: String): List[String] = {
    val xmla: NodeSeq = XML.loadString(xmlStr)
     for {t <- (xmla\"t").toList} yield (<_>{t\"@matter"}  {t\"@measure"}{t\"@rate" match {case z if z.text == "" => (""); case z => ("  "+z.text)}}{t\"@kind" match {case z if z.text == "" => (""); case z => ("  "+z.text)}}</_>.text)
  }

  /*private def write() {
    if (FileUtil.isExternalStorageWritable) {
      val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName());
      val file = new File(dir, "test.txt")
      FileUtil.writeStringAsFile(input.getText.toString, file)
      Toast.makeText(this, "File written", Toast.LENGTH_SHORT).show()
  
      val aaa = Environment.getExternalStorageDirectory.toString
      Toast.makeText(this, aaa, aaa.length()).show()
  
      input setText ""
      output setText ""
    } else
      Toast.makeText(this, "External storage not writable", Toast.LENGTH_SHORT).show()
  }*/

  /*private def read() {
    if (FileUtil.isExternalStorageReadable) {
      val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName)
      val file = new File(dir, "test.txt")
      if (file.exists && file.canRead) {
        output.setText(FileUtil.readFileAsString(file))
        Toast.makeText(this, "File read", Toast.LENGTH_SHORT).show()
      } else
        Toast.makeText(this, "Unable to read file: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show()
    } else
      Toast.makeText(this, "External storage not readable", Toast.LENGTH_SHORT).show()
  }*/

}

/*
import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.widget.Toast
import _root_.android.view.View
import _root_.android.view.View.OnClickListener
import _root_.android.widget.Button

class ScalaActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    import android.widget.Button
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    val button = findViewById(R.id.Button4ToastId).asInstanceOf[Button]
    button.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        Toast.makeText(this, "You have clicked the button",
          Toast.LENGTH_LONG).show()
      }
    })
  }
}
*/
