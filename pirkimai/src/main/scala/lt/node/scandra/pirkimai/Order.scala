package lt.node.scandra.pirkimai

import android.os.Bundle
import android.util.Log
import android.content.res.Resources
import java.text.SimpleDateFormat
import android.view.Menu._
import android.view.{MenuItem, Menu, ViewGroup, View}
import xml._

import android.graphics.Color
import android.widget.AdapterView.OnItemClickListener
import android.content.{Context, Intent}
import android.widget._
import util.{OrderPurchase, FileUtil}
import java.io.{File, InputStream}
import java.util.{ArrayList, HashMap, Date}
import android.app.{Activity, ListActivity}

class Order extends ListActivity with OrderPurchase /*with OnClickListener*/ {

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    logExtras("onCreate ------ ")
    val setTitleTxt = this.getResources.getString(R.string.orderTitle)
    setTitle(setTitleTxt.subSequence(0, setTitleTxt.length))
    //this.setContentView(R.layout.order)
    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
    val tempxmlFile = new File(dir, "tempxml.txt")

    //val thingsString: String = getXmlAsStr(R.raw.thingsxml, "thingsxml.txt", /*"orderString", */this.asInstanceOf[Activity])
    /* val thingsxmlFile = new File(dir, "thingsxml.txt")
    val thingsString: String =
      thingsxmlFile.exists() match {
      case false => // development time case
        Log.v(tagclass, "thingsxmlFile.exists()  case false ...")
        val resources: Resources = this.getResources
        val srcStream: InputStream = resources openRawResource R.raw.thingsxml
        val srcString = scala.io.Source.fromInputStream(srcStream).getLines().mkString("\n")
        FileUtil.writeStringAsFile(srcString, thingsxmlFile) /*: Boolean*/
        FileUtil.readFileAsString(thingsxmlFile)
      case _ => //
        Log.v(tagclass, "thingsxmlFile.exists()  case true ...")
        //        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
        //        val thingsxmlFile = new File(dir, "thingsxml.txt")
        FileUtil.readFileAsString(thingsxmlFile)
    }*/
    //Log.v(tagclass + "case ANY = ", thingsString.toString)

    getIntent match {
      case intent if intent.hasExtra("orderNodeXmlStringDone") =>
        Log.v(tagclass, " case orderNodeXmlStringDone ...")
        //        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
        //        val tempxmlFile = new File(dir, "tempxml.txt")
        val orderNode: Node = XML.loadFile(tempxmlFile)
        addChild(orderNode, ((XML.loadString(getIntent.getStringExtra("orderNodeXmlStringDone"))).
          asInstanceOf[Elem] % new UnprefixedAttribute("s", "2", Null))) match {
          case Some(orderNodeUpdated) =>
            Log.v(tagclass, " case orderNodeXmlStringDone ..." + orderNodeUpdated.toString)
            XML.save(dir + "/tempxml.txt", orderNodeUpdated, "UTF-8", true, null)
            //FileUtil.writeStringAsFile(orderNodeUpdated.toString(), tempxmlFile) /*: Boolean*/
            getIntent.hasExtra("origOrderNodeXmlString") match {
              case true =>
                Log.v(tagclass, " case orderNodeXmlStringDone origOrderNodeXmlString " + getIntent.getStringExtra("origOrderNodeXmlString"))
                XML.save(dir + "/tempxml.txt",
                  removeOrderItem(orderNodeUpdated.toString,
                    orderItem2Str(XML.loadString(getIntent.getStringExtra("origOrderNodeXmlString")), false)),
                  "UTF-8", true, null)
              case _ =>
            }
            if (getIntent.hasExtra("back2PreOrder")) {
              Log.v(tagclass, " case orderNodeXmlStringDone getIntent.hasExtra(\"back2PreOrder\") " + getIntent.hasExtra("back2PreOrder"))
              startActivity(new Intent(this, classOf[PreOrder]))
            }
            startActivity(new Intent(this, classOf[PreOrder]))
          case None =>
            Toast.makeText(getApplicationContext, "Can only add children to elements!", Toast.LENGTH_LONG).show()
            startActivity(new Intent(this, classOf[Main]))
        }

      case intent if intent.hasExtra("newOrder") =>
        Log.v(tagclass, "tempxmlFile.exists()  case false ...")
        val tempXml: Node = <order created={new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())}></order>
        //        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
        //        val tempxmlFile = new File(dir, "tempxml.txt")
        FileUtil.writeStringAsFile(tempXml.toString(), tempxmlFile) /*: Boolean*/

      case intent if intent.hasExtra("resumeOrder") =>
        Toast.makeText(getApplicationContext, "Pirkinių sąrašo formavimas bus tęsiamas", Toast.LENGTH_LONG).show()
        startActivity(new Intent(this, classOf[PreOrder]))

      case intent if intent.hasExtra("createOrder") =>
        //        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
        //        val tempxmlFile = new File(dir, "tempxml.txt")
        tempxmlFile.exists() match {
          case true =>
            Log.v(tagclass, "tempxmlFile.exists()  case true ...")
            //startActivity(new Intent(this, classOf[OrderNewOrResume]).putExtra("createOrder", "yes"))
            startActivity(new Intent(this, classOf[OrderNewOrResume])/*.putExtra("groupId", getIntent.getStringExtra("groupId"))*/)
          case _ => //
            Log.v(tagclass, "tempxmlFile.exists()  case false ...")
            val tempXml: Node = <order created={new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())}></order>
            FileUtil.writeStringAsFile(tempXml.toString(), tempxmlFile) /*: Boolean*/
            startActivity(new Intent(this, classOf[Groups]).
              putExtra("case", "Order"))
        }
      case _ =>
        showThingItems
    }

    /*getIntent.hasExtra("PreOrderNEW") match {
      case true =>
        //fillData()
        showThingItems(/*thingsString*/)
      case false if (XML.loadFile(tempxmlFile) \ "t").length > 0 =>
        startActivity(new Intent(this, classOf[PreOrder])) // C307/vsh -- bus grįžtama į PreOrder pirkių sąrašą
      case _ =>
        //fillData()
        showThingItems(/*thingsString*/)
    }*/

  }


  override def onCreateOptionsMenu(menu: Menu) = {
    super.onCreateOptionsMenu(menu)
    menu.add(NONE, 0, 0, R.string.home) //.setIcon(android.R.drawable.ic_menu_back)
    menu.add(NONE, 1, 1, R.string.menuOrderCreationFinish)
    menu.add(NONE, 2, 2, R.string.menuOrderDisplay)
    //menu.getItem(0).setIcon(R.drawable.red)
    //menu.getItem(1).setIcon(R.drawable.green)
    true
  }

  override def onMenuItemSelected(featureId: Int, item: MenuItem) = {
    super.onMenuItemSelected(featureId, item)
    item.getItemId match {
      case 0 =>
        startActivity(new Intent(Order.this, classOf[Main]))
      case 1 =>
        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
        val thingsxmlFile = new File(dir, "thingsxml.txt")
        val tempxmlFile = new File(dir, "tempxml.txt")
        Log.v(tagclass, " onMenuItemSelected case menuOrderCreationFinish ...")
        if ((XML.loadFile(tempxmlFile) \ "t").length > 0) {
          temp2order(getApplicationContext, FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName))
          Toast.makeText(getApplicationContext, R.string.purchase_ready, Toast.LENGTH_LONG).show()
          startActivity(new Intent(this, classOf[Main]))
        } else {
          Toast.makeText(getApplicationContext, R.string.purchase_empty, Toast.LENGTH_LONG).show()
          startActivity(new Intent(this, classOf[Main]))
        }

      case _ =>
        showPreOrderItems()
      // TODO padaryti peržiūrimų pirkinių taisymą/keitimą ir trynimą
    }
    true
  }


  // def orderCreationFinish(thingsString: String) /*: Unit =*/ { }


//  def fillData() {
//    //-> http://stackoverflow.com/questions/7318765/adding-button-to-each-row-in-listview
//    //-> http://androidforbeginners.blogspot.com/2010/03/clicking-buttons-in-listview-row.html
//    //-> http://eureka.ykyuen.info/2010/01/03/android-simple-listview-using-simpleadapter/
//    //-> http://commonsware.com/Android/excerpt.pdf ?
//    //var lv: ListView = findViewById(R.id.tvViewRow).asInstanceOf[ListView]
//    val from = Array[String]("rowid")
//    val to = Array[Int](R.id.tvTemplateViewRow)
//    var fillMaps: List[HashMap[String, String]] = new ArrayList[HashMap[String, String]]
//
//    val xmlStr: String =
//      getXmlAsStr(R.raw.thingsxml, "thingsxml.txt", "orderString", this.asInstanceOf[Activity])
//    Log.v(TAG + " Templates fillData xmlStr = ", xmlStr.toString)
//    Log.v(TAG + " Templates fillData getStringExtra(\"groupId\") = ", getIntent.getStringExtra("groupId"))
//
//    templateItems(xmlStr, getIntent.getStringExtra("groupId")).toArray[String].foreach(arg => {
//      val map: HashMap[String, String] = new HashMap[String, String]
//      map.put("rowid", arg)
//      fillMaps.add(map)
//    })
//    Log.v(tagclass + " fillData fillMaps size ", fillMaps.size.toString)
//    Log.v(tagclass + " fillData fillMaps ", fillMaps.toString)
//    Log.v(tagclass + " fillData preorder_view_row_x ", templates_view_row_x.toString)
//    this.setListAdapter(new SimpleAdapter(this, fillMaps, templates_view_row_x, from, to))
//  }

  def showThingItems(/*thingsString: String*/) {
    Log.v(tagclass + "showOrderItemsAll", " ... ... ...")
    val lv: ListView = this.getListView
    val thingsString: String =
      getXmlAsStr(R.raw.thingsxml, "thingsxml.txt", /*"orderString",*/ this.asInstanceOf[Activity])
    /*thingItems(thingsString)*/thingItems(thingsString, getIntent.getStringExtra("groupId"))/*.toArray[String]*/  match {
      case List() =>
        Toast.makeText(getApplicationContext,
          "Visai nėra pirkimo ruošinių !!!", Toast.LENGTH_LONG).show()
        val newActivity: Intent = new Intent(this, classOf[Main]);
        startActivity(newActivity)
      case ti =>
        //setListAdapter(new ArrayAdapter[String](this, R.layout.purchase_list_item, orderItemsAll(orderString).toArray))
        setListAdapter(new MyArrayAdapter[String](this, R.layout.order_list_row, ti.toArray))

        //lv.setBackgroundResource(R.drawable.red)
        lv.setBackgroundColor(Color.BLACK)
        lv.setTextFilterEnabled(true)
        lv.setOnItemClickListener(new OnItemClickListener() {
          override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
            //-> http://stackoverflow.com/questions/4852307/listview-onclick-goes-to-a-new-activity
            view.asInstanceOf[TextView].setTextColor(Color.BLUE)
            val selectedItemMatter: String = view.asInstanceOf[TextView].getText.toString.
              substring(0, view.asInstanceOf[TextView].getText.toString.indexOf(" [")).trim()
            val thingNode: Node = getByAtt(XML.loadString(thingsString), "matter", selectedItemMatter).apply(0)
            val thingNodeXmlString: String = thingNode.toString
            val orderNodeXmlString: String =
                <t matter={selectedItemMatter} measure={(thingNode \ "@measure").text}/>.toString

            Log.v(tagclass, "onItemClick ...")
            val omActivity: Intent = new Intent(Order.this, classOf[OrderMaking]);
            omActivity.putExtra("thingNodeXmlString", thingNodeXmlString)
            omActivity.putExtra("orderNodeXmlString", orderNodeXmlString)
            omActivity.putExtra("groupId", getIntent.getStringExtra("groupId"))
            if (getIntent.hasExtra("back2PreOrder")) omActivity.putExtra("back2PreOrder", "yes")
            startActivity(omActivity)
            Log.v(tagclass, "... onItemClick")
            //-- Toast.makeText(getApplicationContext(), view.asInstanceOf[TextView].getText(), Toast.LENGTH_LONG).show()
          }
        })

        //-> http://stackoverflow.com/questions/5399781/change-text-color-in-listview
        class MyArrayAdapter[String](context: Context, textViewResourceId: Int, objects: Array[String with java.lang.Object])
          extends ArrayAdapter[String](context: Context, textViewResourceId: Int, objects: Array[String with java.lang.Object]) {
          override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
            val view: View = super.getView(position, convertView, parent)
            val tv = view.asInstanceOf[TextView]
            tv.setTextColor(Color.WHITE)
            view
          }
        }
    }
  }


  def showPreOrderItems() /*: Unit =*/ {
    Log.v(tagclass + " showPreorderItems  ", " ... ... ...")
    val newActivity: Intent = new Intent(this, classOf[PreOrder]);
    //newActivity.putExtra("orderString", orderString)
    //newActivity.putExtra("selectedItem", view.asInstanceOf[TextView].getText())
    startActivity(newActivity)
  }


//  def showPreorderItemsXXX() /*: Unit =*/ {
//    Log.v(tagclass + " showPreorderItems  ", " ... ... ...")
//    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
//    //val tempxmlFile = new File(dir, "tempxml.txt")
//    //val orderxmlFile = new File(dir, "tempxml.txt")
//    val tempXmlString = FileUtil.readFileAsString(new File(dir, "tempxml.txt"))
//
//    preorderItems(tempXmlString) match {
//      case List() =>
//        Toast.makeText(getApplicationContext,
//          "Visai nėra pirkimo ruošinių !!!", Toast.LENGTH_LONG).show()
//        val newActivity: Intent = new Intent(this, classOf[Main]);
//        startActivity(newActivity)
//      case ti =>
//        setListAdapter(new MyArrayAdapter[String](this, R.layout.purchase_list_item, preorderItems(tempXmlString).toArray))
//        var lv: ListView = this.getListView
//        lv.setBackgroundResource(R.drawable.red)
//        lv.setTextFilterEnabled(true)
//        lv.setOnItemClickListener(new OnItemClickListener() {
//          override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
//            //-> http://stackoverflow.com/questions/4852307/listview-onclick-goes-to-a-new-activity
//            /*Log.v(tagclass + " Purchase", "onItemClick ...")
//            val newActivity: Intent = new Intent(Purchase.this, classOf[PurchaseDecision]);
//            newActivity.putExtra("orderString", orderString)
//            newActivity.putExtra("selectedItem", view.asInstanceOf[TextView].getText())
//            startActivity(newActivity)
//            Log.v(tagclass + " Purchase", "... onItemClick")*/
//            Toast.makeText(getApplicationContext(),
//              view.asInstanceOf[TextView].getText(), Toast.LENGTH_LONG).show()
//          }
//        })
//    }
//
//    //-> http://stackoverflow.com/questions/5399781/change-text-color-in-listview
//    class MyArrayAdapter[String](context: Context, textViewResourceId: Int, objects: Array[String with java.lang.Object])
//      extends ArrayAdapter[String](context: Context, textViewResourceId: Int, objects: Array[String with java.lang.Object]) {
//      override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
//        val view: View = super.getView(position, convertView, parent)
//        val tv = view.asInstanceOf[TextView]
//        tv.setTextColor(Color.WHITE)
//        view
//      }
//    }
//  }

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
    logExtras(extras, msg)
  }


}

