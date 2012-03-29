package lt.node.scandra.pirkimai

import android.os.Bundle

import android.widget._
import java.util.{List, ArrayList, HashMap}
import android.util.Log
import android.app.{ListActivity}
import android.view.View.OnClickListener
import util.{FileUtil, OrderPurchase}
import android.view.Menu._
import android.content.res.{Resources, Configuration}
import java.io.{InputStream, File}
import xml.{Elem, Node, XML}
import android.content.{Context, Intent}
import android.view._

class PreOrder extends ListActivity with OnClickListener with OrderPurchase {

  private[this] var preorder_view_row_x: Int = R.layout.preorder_view_row

//  private[this] var btn: Button = _
  private[this] var context: Context = _

  override def onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig);
    preorder_view_row_x = newConfig.orientation match {
      case Configuration.ORIENTATION_LANDSCAPE =>
        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        R.layout.preorder_view_row_land
      case Configuration.ORIENTATION_PORTRAIT =>
        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        R.layout.preorder_view_row
      case _ =>
        Toast.makeText(this, "NOT {landscape portrait} !!!", Toast.LENGTH_LONG).show()
        R.layout.preorder_view_row
    }
    fillData()
  }

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.preorder);
    context = this.getApplicationContext

//    btn = findViewById(R.id.goto_things).asInstanceOf[Button]
    //val view: View = new View() // getLayoutInflater()..inflate(R.layout.goto_things, null)
    //this.getListView.addFooterView(btn, null, true)
//    val footerView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//      .asInstanceOf[LayoutInflater].inflate(R.layout.goto_things, null, true)
    val inflater: LayoutInflater = getLayoutInflater()
    val footerView: View = inflater.inflate(R.layout.goto_things, findViewById(R.id.things_footer_layout).asInstanceOf[ViewGroup])
    getListView.addFooterView(footerView, null, true)
    fillData()
 }


  override def onCreateOptionsMenu(menu: Menu) = {
    super.onCreateOptionsMenu(menu)
    menu.add(NONE, 0, 0, R.string.home) //.setIcon(android.R.drawable.ic_menu_back)
    menu.add(NONE, 1, 1, R.string.menuOrderCreationFinish)
    //    menu.add(NONE, 1, 1, R.string.new_goods_type).setIcon(android.R.drawable.ic_menu_add /*ic_menu_compose*/)
    //    menu.add(NONE, 1, 1, R.string.results_done)
    //    menu.add(NONE, 2, 2, R.string.results_partially_done)
    true
  }

  override def onMenuItemSelected(featureId: Int, item: MenuItem) = {
    super.onMenuItemSelected(featureId, item)
    //setUomChoice(if (item.getItemId == 1) METRIC else ENGLISH)
    //val node: Node = XML.loadString(orderString)
    //val orderString: String =
    //FileUtil.readFileAsString(new File(FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName()), "orderxml.txt"))
    item.getItemId match {
      case 0 =>
        startActivity(new Intent(PreOrder.this, classOf[Main]))
        //Toast.makeText(getApplicationContext(), "Apie ...", Toast.LENGTH_LONG).show()
        // TO DO padaryti apk'o mini aprašėlį
      case 1 =>
        Log.v(this.TAG + " PreOrder", " onMenuItemSelected case menuOrderCreationFinish ...")
        temp2order(getApplicationContext, FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName))
        startActivity(new Intent(this, classOf[Main]))
      case _ =>
    }
    true
  }

  def onClick(view: View) {
    editClickHandler(view)
    editClickTextViewHandler(view)
    deleteClickHandler(view)
    thingsClickHandler(view)
  }
//  def onClick(view: View) {
//    view match {
//      case v if view equals btn =>
//        Log.v(this.TAG + " PreOrder", " onClick btn ...")
//        startActivity(new Intent(this, classOf[Order])/*.putExtra("createOrder", "yes")*/)
//      case _ =>
//        editClickHandler(view)
//        deleteClickHandler(view)
//        thingsClickHandler(view)
//    }
//  }



  def editClickTextViewHandler(v: View) {
    Log.v(TAG + " PreOrder", "editClickTextViewHandler ...")
    //get the row the clicked button is in
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val txtChild = vwParentRow.getChildAt(1).asInstanceOf[TextView]

    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
    val thingsxmlFile = new File(dir, "thingsxml.txt")
    val thingsString: String = thingsxmlFile.exists() match {
      case false => // development time case
        Log.v(TAG + " PreOrder", " editClickTextViewHandler thingsxmlFile.exists()  case false ...")
        val resources: Resources = this.getResources
        val srcStream: InputStream = resources openRawResource R.raw.thingsxml
        val srcString = scala.io.Source.fromInputStream(srcStream).getLines().mkString("\n")
        FileUtil.writeStringAsFile(srcString, thingsxmlFile) /*: Boolean*/
        FileUtil.readFileAsString(thingsxmlFile)
      case _ => //
        Log.v(TAG + " PreOrder", " editClickTextViewHandler thingsxmlFile.exists()  case true ...")
        val thingsxmlFile = new File(dir, "thingsxml.txt")
        FileUtil.readFileAsString(thingsxmlFile)
    }
    Log.v(this.TAG + " PreOrder editClickTextViewHandler tempxml.txt ", FileUtil.readFileAsString(new File(dir, "tempxml.txt"))  )
    Log.v(this.TAG + " PreOrder editClickTextViewHandler txtChild.asInstanceOf[TextView].getText.toString ", txtChild.asInstanceOf[TextView].getText.toString  )
    Log.v(this.TAG + " PreOrder editClickTextViewHandler txtChild.asInstanceOf[TextView].getText.toString ", getOrderItemByItemString(
      FileUtil.readFileAsString(new File(dir, "tempxml.txt")),
      txtChild.asInstanceOf[TextView].getText.toString).toString )

    getOrderItemByItemString( FileUtil.readFileAsString(new File(dir, "tempxml.txt")),
      txtChild.asInstanceOf[TextView].getText.toString) match {
      case Some(oibis) =>
//        val orderNodeXml: Elem = getOrderItemByItemString(
//          FileUtil.readFileAsString(new File(dir, "tempxml.txt")),
//          txtChild.asInstanceOf[TextView].getText.toString).get.asInstanceOf[Elem]
        val orderNodeXml: Elem = oibis.asInstanceOf[Elem]
        Log.v(this.TAG + " PreOrder editClickTextViewHandler ", orderNodeXml.toString)
        val selectedItemMatter: String = (orderNodeXml\"@matter")(0).toString
        val thingNode: Node = getByAtt(XML.loadString(thingsString), "matter", selectedItemMatter).apply(0)
        val thingNodeXmlString: String = thingNode.toString

        val orderNodeXmlString: String = orderNodeXml.toString

        val omActivity: Intent = new Intent(PreOrder.this, classOf[OrderMaking]);
        omActivity.putExtra("thingNodeXmlString", thingNodeXmlString)
        omActivity.putExtra("orderNodeXmlString", orderNodeXmlString)
        omActivity.putExtra("origOrderNodeXmlString", orderNodeXmlString)
        omActivity.putExtra("back2PreOrder", "yes")
        startActivity(omActivity)

        Toast.makeText(getApplicationContext, txtChild.getText, Toast.LENGTH_LONG).show()

        //vwParentRow.setBackgroundColor(Color.GREEN)
        //vwParentRow.refreshDrawableState()
        Log.v(TAG + " PreOrder", "... editClickTextViewHandler")
        fillData
      case _ =>
        Log.e(TAG + " PreOrder myClickHandler ",
          this.getResources().getString(R.string.errGetOrderItemByItemStringReturnsNone))
        Toast.makeText(getApplicationContext,
          this.getResources().getString(R.string.errGetOrderItemByItemStringReturnsNone), Toast.LENGTH_LONG).show()
        startActivity(new Intent(this, classOf[Main]))
    }

  }


  def editClickHandler(v: View) {
    Log.v(TAG + " PreOrder", "editClickHandler ...")
   //reset all the listView items background colours before we set the clicked one..
    //    val lvItems: ListView = getListView
    /*for (int i=0; i<lvItems.getChildCount(); i++) {
      lvItems.getChildAt(i).setBackgroundColor(Color.BLUE); }*/

    //get the row the clicked button is in
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val txtChild = vwParentRow.getChildAt(1).asInstanceOf[TextView]
    //txtChild.setWidth(tvWidth)
    //val btnChild = vwParentRow.getChildAt(0).asInstanceOf[ImageButton]
    //btnChild.setPadding(9,9,9,9)  //.setText(txtChild.getText)

    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
    val thingsxmlFile = new File(dir, "thingsxml.txt")
    val thingsString: String = thingsxmlFile.exists() match {
      case false => // development time case
        Log.v(TAG + " PreOrder", " editClickHandler thingsxmlFile.exists()  case false ...")
        val resources: Resources = this.getResources
        val srcStream: InputStream = resources openRawResource R.raw.thingsxml
        val srcString = scala.io.Source.fromInputStream(srcStream).getLines().mkString("\n")
        FileUtil.writeStringAsFile(srcString, thingsxmlFile) /*: Boolean*/
        FileUtil.readFileAsString(thingsxmlFile)
      case _ => //
        Log.v(TAG + " PreOrder", " editClickHandler thingsxmlFile.exists()  case true ...")
        val thingsxmlFile = new File(dir, "thingsxml.txt")
        FileUtil.readFileAsString(thingsxmlFile)
    }
    Log.v(this.TAG + " PreOrder myClickHandler 00 tempxml.txt ", FileUtil.readFileAsString(new File(dir, "tempxml.txt"))  )
    Log.v(this.TAG + " PreOrder myClickHandler 01 txtChild.asInstanceOf[TextView].getText.toString ", txtChild.asInstanceOf[TextView].getText.toString  )
    Log.v(this.TAG + " PreOrder myClickHandler 02 txtChild.asInstanceOf[TextView].getText.toString ", getOrderItemByItemString(
      FileUtil.readFileAsString(new File(dir, "tempxml.txt")),
      txtChild.asInstanceOf[TextView].getText.toString).toString )

    getOrderItemByItemString( FileUtil.readFileAsString(new File(dir, "tempxml.txt")),
      txtChild.asInstanceOf[TextView].getText.toString) match {
      case Some(oibis) =>
//        val orderNodeXml: Elem = getOrderItemByItemString(
//          FileUtil.readFileAsString(new File(dir, "tempxml.txt")),
//          txtChild.asInstanceOf[TextView].getText.toString).get.asInstanceOf[Elem]
        val orderNodeXml: Elem = oibis.asInstanceOf[Elem]
        Log.v(this.TAG + " PreOrder myClickHandler ", orderNodeXml.toString)
        val selectedItemMatter: String = (orderNodeXml\"@matter")(0).toString
        val thingNode: Node = getByAtt(XML.loadString(thingsString), "matter", selectedItemMatter).apply(0)
        val thingNodeXmlString: String = thingNode.toString

        val orderNodeXmlString: String = orderNodeXml.toString

        val omActivity: Intent = new Intent(PreOrder.this, classOf[OrderMaking]);
        omActivity.putExtra("thingNodeXmlString", thingNodeXmlString)
        omActivity.putExtra("orderNodeXmlString", orderNodeXmlString)
        omActivity.putExtra("origOrderNodeXmlString", orderNodeXmlString)
        omActivity.putExtra("back2PreOrder", "yes")
        startActivity(omActivity)

        Toast.makeText(getApplicationContext, txtChild.getText, Toast.LENGTH_LONG).show()

        //vwParentRow.setBackgroundColor(Color.GREEN)
        //vwParentRow.refreshDrawableState()
        Log.v(TAG + " PreOrder", "... editClickHandler")
        fillData
      case _ =>
        Log.e(TAG + " PreOrder myClickHandler ",
          this.getResources().getString(R.string.errGetOrderItemByItemStringReturnsNone))
        Toast.makeText(getApplicationContext,
          this.getResources().getString(R.string.errGetOrderItemByItemStringReturnsNone), Toast.LENGTH_LONG).show()
        startActivity(new Intent(/*PreOrder.*/this, classOf[Main]))
    }

  }


  def deleteClickHandler(v: View) {
    //get the row the clicked button is in
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val txtChild = vwParentRow.getChildAt(1).asInstanceOf[TextView]
    //val btnChild = vwParentRow.getChildAt(2).asInstanceOf[ImageButton]
    //btnChild.setPadding(9,9,9,9)  //.setText(txtChild.getText)
    Toast.makeText(getApplicationContext, txtChild.getText, Toast.LENGTH_LONG).show()
    Toast.makeText(getApplicationContext, keyOfListViewItem(txtChild.getText.toString), Toast.LENGTH_LONG).show()
    //vwParentRow.setBackgroundColor(Color.RED)

    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName)
    val xmlStr = FileUtil.readFileAsString(new File(dir, "tempxml.txt"))
    XML.save(dir + "/tempxml.txt", removeOrderItem(xmlStr, txtChild.getText.toString), "UTF-8", true, null)
    //vwParentRow.refreshDrawableState()
    fillData
  }


  def thingsClickHandler(v: View) {
    Log.v(this.TAG + " PreOrder", " thingsClickHandler ...")
    /*startActivity(new Intent(this, classOf[Order]).
      putExtra("back2PreOrder", "yes").
      putExtra("PreOrderNEW", "yes"))*/
    startActivity(new Intent(this, classOf[Groups]).
      putExtra("case", "Order"))
  }


  def fillData() {
    //-> http://stackoverflow.com/questions/7318765/adding-button-to-each-row-in-listview
    //-> http://androidforbeginners.blogspot.com/2010/03/clicking-buttons-in-listview-row.html
    //-> http://eureka.ykyuen.info/2010/01/03/android-simple-listview-using-simpleadapter/
    //-> http://commonsware.com/Android/excerpt.pdf ?
    //var lv: ListView = findViewById(R.id.tvViewRow).asInstanceOf[ListView]
    val from = Array[String]("rowid" /*, "btn"*/)
    val to = Array[Int](R.id.tvViewRow /*, R.id.btnToClick*/)
    var fillMaps: List[HashMap[String, String]] = new ArrayList[HashMap[String, String]]
    /*var i: Int = 0
    while (i < 10) {
      {
        var map: HashMap[String, String] = new HashMap[String, String]
        map.put("rowid", "000" + i)
        fillMaps.add(map)
      }
      ({
        i += 1;
        i
      })
    }*/

    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName)
    val xmlStr = FileUtil.readFileAsString(new File(dir, "tempxml.txt"))
    preorderItems(xmlStr).toArray[String].foreach(arg => {
      val map: HashMap[String, String] = new HashMap[String, String]
      map.put("rowid", arg)
      fillMaps.add(map)
    })
    Log.v(this.TAG + " PreOrder fillData fillMaps size ", fillMaps.size.toString)
    Log.v(this.TAG + " PreOrder fillData fillMaps ", fillMaps.toString)
    Log.v(this.TAG + " PreOrder fillData preorder_view_row_x ", preorder_view_row_x.toString)
    this.setListAdapter(new SimpleAdapter(this, fillMaps, preorder_view_row_x, from, to))
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

