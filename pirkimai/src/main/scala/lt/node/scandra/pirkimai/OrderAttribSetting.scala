package lt.node.scandra.pirkimai

//-> https://github.com/hyone/android-listview-scala  <-- some useful info

import android.app.ListActivity
import android.widget.AdapterView._
import android.os.Bundle
import util.OrderPurchase

import android.widget._
import android.util.Log
import android.graphics.Color
import android.view.{ViewGroup, View}
import android.content.{Context, Intent}


class OrderAttribSetting extends ListActivity with OrderPurchase {

  private[this] var thingNodeXmlString: String = _
  private[this] var orderNodeXmlString: String = _
  private[this] var attribName: String = _
//  private[this] var attribValue: String = _


  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    //this.setContentView(R.layout.order)

    /*this.internalStorage = findViewById(R.id.main_internal_storage_button).asInstanceOf[Button]
    this.internalStorage setOnClickListener this
    this.externalStorage = findViewById(R.id.main_external_storage_button).asInstanceOf[Button]
    this.externalStorage setOnClickListener this*/

//    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
//    val thingsxmlFile = new File(dir, "thingsxml.txt")
//    val thingsString: String = thingsxmlFile.exists() match {
//      case false => // development time case
//        Log.v(TAG + " Order", "thingsxmlFile.exists()  case false ...")
//        val resources: Resources = this.getResources
//        val srcStream: InputStream = resources openRawResource R.raw.thingsxml
//        val srcString = scala.io.Source.fromInputStream(srcStream).getLines().mkString("\n")
//        FileUtil.writeStringAsFile(srcString, thingsxmlFile) /*: Boolean*/
//        FileUtil.readFileAsString(thingsxmlFile)
//      case _ =>  //
//        Log.v(TAG + " Order", "thingsxmlFile.exists()  case true ...")
//        """<goto_things updated="2012-02-15 19:10">
//            <t matter="pienas" measure="l" qty="0.5|1|1.5|2|3|4" fatness="2.5%|3.5%">
//                <stat lastuse="2012-02-15 19:10" updated="2012-02-15 19:10" created="2012-02-15 19:10" usecases="0" />
//            </t>
//            <t  matter="kefyras" measure="l" qty="0.5|1|1.5|2|3|4" fatness="2.5%|3.5%|" >
//                <stat lastuse="2012-02-15 19:10" updated="2012-02-15 19:10" created="2012-02-15 19:10" usecases="0" />
//            </t>
//            <t  matter="jogurtas" measure="l" name="Vaisinis|Šokoladinis"  qty="0.5|1|1.5|2|3|4" fatness="2.5%|3.5%|">
//                <stat lastuse="2012-02-15 19:10" updated="2012-02-15 19:10" created="2012-02-15 19:10" usecases="0" />
//            </t>
//            <t  matter="bulvės" measure="kg" qty="0.5|1|1.5|2|3|4|_" name="Baltos|Prūsinės">
//                <stat lastuse="2012-02-15 19:10" updated="2012-02-15 19:10" created="2012-02-15 19:10" usecases="0" />
//            </t>
//            <t  matter="žirniai" measure="g" qty="200|400|800" name="Galinta" kind="šlifuoti|skaldyti">
//                <stat lastuse="2012-02-15 19:10" updated="2012-02-15 19:10" created="2012-02-15 19:10" usecases="0" />
//            </t>
//            <t  matter="alus" measure="l" qty=".33|.5|1.0|1.5" name="Šaltukas|Extra|7.30|Tauras|Utenos|Kauno|Biržų|Gubernijos|Velkopopovicky Kozel|Germania|" kind="stikl.but.|skardinės|bambaliai">
//                <stat lastuse="YYYY-MM-DD HH:mm" updated="YYYY-MM-DD HH:mm" created="YYYY-MM-DD HH:mm" usecases="0" />
//            </t>
//            <t  matter="ypatingasis" measure="..." qty="..." >
//                <stat lastuse="YYYY-MM-DD HH:mm" updated="YYYY-MM-DD HH:mm" created="YYYY-MM-DD HH:mm" usecases="0" />
//            </t>
//        </goto_things>
//        """
//    }
//    Log.v(TAG + " Order ... case ANY = ", thingsString.toString)
//    showThingItems(thingsString)

    thingNodeXmlString = getIntent.getStringExtra("thingNodeXmlString")
    orderNodeXmlString = getIntent.getStringExtra("orderNodeXmlString")
    attribName = getIntent.getStringExtra("attrib")
    //attribValue = getIntent.getStringExtra("values")    // attribValue

    var attribValuesStr: String = getIntent.getStringExtra("values").trim
    attribValuesStr = if (attribValuesStr.startsWith("_")) attribValuesStr else  "_|" + attribValuesStr
    attribValuesStr = if (attribValuesStr.endsWith("...")) attribValuesStr else  attribValuesStr + "|..."
    val attribValues: Array[String] = attribValuesStr.split("\\|")

    Log.v(TAG + " OrderAttribSetting ... case ANY = ", <_>attrib={attribName} attribValues={attribValues.toList}</_>.text)
    showAttribValues(attribValues)
  }


  def showAttribValues(attribValues: Array[String]) {
    Log.v(TAG + " OrderAttribSetting showAttribValues  ", " ... ... ...")
    attribValues match {
      case av if av.length == 0 =>
        Toast.makeText(getApplicationContext,
          <_>Klaida: nėra reikšmių atributui {getIntent.getStringExtra("attrib")} !!!"</_>.text, Toast.LENGTH_LONG).show()
        val newActivity: Intent = new Intent(this, classOf[Main]);
        startActivity(newActivity)
      case ti =>
        //setListAdapter(new ArrayAdapter[String](this, R.layout.purchase_list_item, orderItemsAll(orderString).toArray))
        setListAdapter(new MyArrayAdapter[String](this, R.layout.thing_list_item, ti/*.toArray*/))

        val lv: ListView = this.getListView
        //lv.setBackgroundResource(R.drawable.red)
        lv.setBackgroundColor(Color.BLACK)
        //         Log.v(TAG + " Purchase showOrderItemsAll getCount  ", lv.getCount().toString)
        //         for (i <- 0 to lv.getCount()) {
        //           Log.v(TAG + " Purchase showOrderItemsAll  ", lv.getItemAtPosition(i).getClass.toString)
        //           Log.v(TAG + " Purchase showOrderItemsAll  ", lv.getItemAtPosition(i).asInstanceOf[String])
        //           //Log.v(TAG + " Purchase showOrderItemsAll  ", lv.getItemAtPosition(i)./.*asInstanceOf[View]*//*.getChildAt(i)*/toString)
        //           // //lv.getAdapter.getItemIdAtPosition(i)
        //           //lv.getTouchables.get(i).setBackgroundColor(Color.GREEN)
        //           //lv.getChildAt(i).setBackgroundColor(Color.GREEN)
        //           //
        //         }
        lv.setTextFilterEnabled(true)
        lv.setOnItemClickListener(new OnItemClickListener() {
          override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
            //-> http://stackoverflow.com/questions/4852307/listview-onclick-goes-to-a-new-activity
            Log.v(TAG + " OrderAttribSetting", "onItemClick ...")
            val omActivity: Intent = new Intent(OrderAttribSetting.this, classOf[OrderMaking]);
            omActivity.putExtra("thingNodeXmlString", thingNodeXmlString)
            omActivity.putExtra("orderNodeXmlString", orderNodeXmlString)
            omActivity.putExtra("attrib", attribName)
            omActivity.putExtra("value", view.asInstanceOf[TextView].getText)
            getIntent.hasExtra("origOrderNodeXmlString") match {
              case true =>
                Log.v(TAG + " OrderAttribSetting showAttribValues ", "onItemClick origOrderNodeXmlString ...")
                omActivity.putExtra("origOrderNodeXmlString", getIntent.getStringExtra("origOrderNodeXmlString"))
              case _ =>
            }
            if (getIntent.hasExtra("back2PreOrder")) omActivity.putExtra("back2PreOrder", "yes")
            startActivity(omActivity)
            Log.v(TAG + " OrderAttribSetting", "... onItemClick")
            //-- Toast.makeText(getApplicationContext(), view.asInstanceOf[TextView].getText(), Toast.LENGTH_LONG).show()
          }
        })
    }

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

