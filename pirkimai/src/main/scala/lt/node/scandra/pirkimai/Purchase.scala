package lt.node.scandra.pirkimai

// https://github.com/hyone/android-listview-scala  <-- some useful info

import android.app.ListActivity
import android.widget.AdapterView._
//import android.widget.ArrayAdapter
import android.os.Bundle
import lt.node.scandra.pirkimai.util.FileUtil
import android.content.res.Resources
import java.io.{InputStream, File}
import util.OrderPurchase

import android.widget._
import android.util.Log
import android.view.Menu._
import xml.{Node, XML}
import android.graphics.Color
import android.view.{ViewGroup, MenuItem, Menu, View}
import android.content.{Context, Intent}


class Purchase extends ListActivity with OrderPurchase {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

/*
    val orderString: String = getIntent().hasExtra("orderString") match {
      case false =>
        Log.v(TAG + " Purchase", "case false ...")
        val resources: Resources = this.getResources
        val srcStream: InputStream = resources openRawResource R.raw.orderxml
        val srcString = scala.io.Source.fromInputStream(srcStream).getLines().mkString("\n")

        //->  http://stackoverflow.com/questions/5221524/idiomatic-way-to-convert-an-inputstream-to-a-string-in-scala
        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName());
        val dstFile = new File(dir, "orderxml.txt")
        FileUtil.writeStringAsFile(srcString, dstFile) /*: Boolean*/

        val orderFile = new File(dir, "orderxml.txt")
        FileUtil.readFileAsString(orderFile)
      case true =>
        Log.v(TAG + " Purchase", "case true ...")
        getIntent().getStringExtra("orderString")
    }
*/

    //->  http://stackoverflow.com/questions/5221524/idiomatic-way-to-convert-an-inputstream-to-a-string-in-scala
    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
    val orderxmlFile = new File(dir, "orderxml.txt")
    val orderString: String = orderxmlFile.exists() match {
      case false => // development time case
        Log.v(TAG + " Purchase", "orderxmlFile.exists()  case false ...")
        val resources: Resources = this.getResources
        val srcStream: InputStream = resources openRawResource R.raw.orderxml
        val srcString = scala.io.Source.fromInputStream(srcStream).getLines().mkString("\n")
        FileUtil.writeStringAsFile(srcString, orderxmlFile) /*: Boolean*/
        FileUtil.readFileAsString(orderxmlFile)
      case _ =>  //
        Log.v(TAG + " Purchase", "orderxmlFile.exists()  case true ...")
        getIntent.hasExtra("orderString") match {
          case false =>
            Log.v(TAG + " Purchase", "case _ case false ...")
            FileUtil.readFileAsString(orderxmlFile)
          case true =>
            Log.v(TAG + " Purchase", "case _ case true ...")
            getIntent.getStringExtra("orderString")
        }
    }
    Log.v(TAG + " Purchase ... case ANY = ", orderString.toString)
    showOrderItemsAll(orderString)
/*
    orderItems(orderString) match {
      case List() =>
        Toast.makeText(getApplicationContext,
          "Valio !!! Viską nupirkau !!!",
          Toast.LENGTH_LONG).show()
        val newActivity: Intent = new Intent(Purchase.this, classOf[Main]);
        startActivity(newActivity)
        // TO DO synchronize  "orderxml.txt"
      case oi =>
        setListAdapter(new ArrayAdapter[String](this, R.layout.purchase_list_item, orderItems(orderString).toArray))
        var lv: ListView = this.getListView
        lv.setTextFilterEnabled(true)
        lv.setOnItemClickListener(new OnItemClickListener() {
          override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
            //-> http://stackoverflow.com/questions/4852307/listview-onclick-goes-to-a-new-activity
            Log.v(TAG + " Purchase", "onItemClick ...")
            val newActivity: Intent = new Intent(Purchase.this, classOf[PurchaseDecision]);
            newActivity.putExtra("orderString", orderString)
            newActivity.putExtra("selectedItem", view.asInstanceOf[TextView].getText())
            startActivity(newActivity)
            Log.v(TAG + " Purchase", "... onItemClick")
            //-- Toast.makeText(getApplicationContext(), view.asInstanceOf[TextView].getText(), Toast.LENGTH_LONG).show()
          }
        })
    }
*/

  }


  override def onCreateOptionsMenu(menu: Menu) = {
    super.onCreateOptionsMenu(menu)
    menu.add(NONE, 0, 0, R.string.home) //.setIcon(android.R.drawable.ic_menu_back)
//    menu.add(NONE, 1, 1, R.string.results_notyet)
//    menu.add(NONE, 2, 2, R.string.results_done)
//    menu.add(NONE, 3, 3, R.string.results_partially_done)
//    menu.add(NONE, 4, 4, R.string.results)
    //menu.getItem(0).setIcon(R.drawable.download)
//    menu.getItem(1).setIcon(R.drawable.red)
//    menu.getItem(2).setIcon(R.drawable.green)
//    menu.getItem(3).setIcon(R.drawable.yellow)
    true
  }

  override def onMenuItemSelected(featureId: Int, item: MenuItem) = {
    super.onMenuItemSelected(featureId, item)
    val orderString: String =
      FileUtil.readFileAsString(
        new File(FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName()), "orderxml.txt"))
    item.getItemId match {
      case 0 =>
        startActivity(new Intent(Purchase.this, classOf[Main]))
//      case 1 =>
//        showOrderItems(orderString)
//      case 2 =>
//        showOrderItemsDone(orderString)
//      case 3 =>
//        showOrderItemsPartiallyDone(orderString)
//      case _ =>
//        showOrderItemsAll(orderString)
    }
    true
  }

  def showOrderItemsAll(orderString: String): Unit = {
    Log.v(TAG + " Purchase showOrderItemsAll  ", " ... ... ...")
    orderItemsAll(orderString) match {
       case List() =>
         Toast.makeText(getApplicationContext,
           "Hey !!! Ar tikrai nieko nereikia nupirkti? !!!",
           Toast.LENGTH_LONG).show()
         val newActivity: Intent = new Intent(Purchase.this, classOf[Main]);
         startActivity(newActivity)
       // TODO synchronize  "orderxml.txt"
       case oi =>
         Log.v(TAG + " Purchase showOrderItemsAll oi ", oi.toString)
         //setListAdapter(new ArrayAdapter[String](this, R.layout.purchase_list_item, orderItemsAll(orderString).toArray))
         setListAdapter(new MyArrayAdapter[String](this, R.layout.purchase_list_item, /*orderItemsAll(orderString)*/oi.toArray))

         val lv: ListView = this.getListView
         //lv.setBackgroundResource(R.drawable.red)
         lv.setBackgroundColor(Color.BLACK)
         lv.setTextFilterEnabled(true)
         lv.setOnItemClickListener(new OnItemClickListener() {
           override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
             //-> http://stackoverflow.com/questions/4852307/listview-onclick-goes-to-a-new-activity
             Log.v(TAG + " Purchase", "onItemClick ...")
             val newActivity: Intent = new Intent(Purchase.this, classOf[PurchaseDecision]);
             newActivity.putExtra("orderString", orderString)
             newActivity.putExtra("selectedItem", view.asInstanceOf[TextView].getText())
             startActivity(newActivity)
             Log.v(TAG + " Purchase", "... onItemClick")
             //-- Toast.makeText(getApplicationContext(), view.asInstanceOf[TextView].getText(), Toast.LENGTH_LONG).show()
           }
         })
     }
    
    //-> http://stackoverflow.com/questions/5399781/change-text-color-in-listview
    class MyArrayAdapter[String](context: Context, textViewResourceId: Int, objects: Array[String with java.lang.Object])
      extends ArrayAdapter[String](context: Context, textViewResourceId: Int, objects: Array[String with java.lang.Object]) {
      override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
        val v: View = super.getView(position, convertView, parent)
        val tv = v.asInstanceOf[TextView]
        val tvGetText = tv.getText.toString
        //Log.v(TAG + " Purchase", "MyArrayAdapter tvGetText=" + tvGetText)
        val text2Display = tvGetText.substring(0, tvGetText.length()-(STATUSPREFIX.length+1+2))
        //Log.v(TAG + " Purchase", "MyArrayAdapter text2Display=" + text2Display)
        val subseq2Display = text2Display.subSequence(0, text2Display.length())
        //Log.v(TAG + " Purchase", "MyArrayAdapter subseq2Display=" + subseq2Display)
        tvGetText match {
          case txt if txt.contains(STATUSPREFIX+"2") =>
            tv.setTextColor(Color.RED)
            tv.setText(subseq2Display)
          case txt if txt.contains(STATUSPREFIX+"1") =>
            tv.setTextColor(Color.YELLOW)
            tv.setText(subseq2Display)
          case txt if txt.contains(STATUSPREFIX+"0") =>
            tv.setTextColor(Color.GREEN)
            tv.setText(subseq2Display)
          case _ =>
            tv.setTextColor(Color.BLUE)
            tv.setText(subseq2Display)
        }
        v
      }
    }
  }

  def tagclass: String = (TAG + " " + this.getClass.getSimpleName + " ")

  def logExtras(extras: scala.List[AnyRef], msg: String)  {
    extras.foreach(e => {
      val ee: String = e.asInstanceOf[String]
      val value =  if (getIntent.getStringExtra(ee) == null) "--null--" else getIntent.getStringExtra(ee)
      Log.v(tagclass + msg + " " + ee, value)
    })
  }
  def logExtras(msg: String)  {
    val extras: scala.List[AnyRef] = getIntent.getExtras.keySet().toArray.toList
    logExtras(extras/*.map(e => e.asInstanceOf[String])*/, msg)
  }

}

