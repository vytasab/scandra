package lt.node.scandra.pirkimai

import android.os.Bundle

import android.widget._
import java.util.{List, ArrayList, HashMap}
import android.util.Log
import android.view.View.OnClickListener
import android.view.Menu._
import android.content.res.Configuration
import android.content.{Context, Intent}
import android.view._
import util._
import android.app.{Activity, ListActivity}
import xml.{NodeSeq, Elem, Node, XML}
import xml.transform.{RuleTransformer, RewriteRule}
import android.graphics.Color

class Templates extends ListActivity with OnClickListener with OrderPurchase with ScalaFunPro with FindView {

  private[this] var etMatter: EditText = _
  private[this] var spinMeasure: Spinner = _
  private[this] var bntCreate: Button = _
  private[this] var bntContinue: Button = _
  private[this] var templates_view_row_x: Int = R.layout.templates_view_row

  //  private[this] var btn: Button = _
  private[this] var context: Context = _
  private[this] var thingsxmlFile: String = _

  override def onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig);
    templates_view_row_x = newConfig.orientation match {
      case Configuration.ORIENTATION_LANDSCAPE =>
        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        R.layout.templates_view_row_land
      case Configuration.ORIENTATION_PORTRAIT =>
        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        R.layout.templates_view_row
      case _ =>
        Toast.makeText(this, "NOT {landscape portrait} !!!", Toast.LENGTH_LONG).show()
        R.layout.templates_view_row
    }
    fillData()
  }

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    context = this.getApplicationContext
    setContentView(R.layout.templates);
    val footerView: View =
      getLayoutInflater.inflate(R.layout.template_list_footer,
        findViewById(R.id.templates_footer_layout).asInstanceOf[ViewGroup])
    getListView.addFooterView(footerView, null, true)
    fillData()
  }


  override def onCreateOptionsMenu(menu: Menu) = {
    super.onCreateOptionsMenu(menu)
    menu.add(NONE, 0, 0, R.string.home) //.setIcon(android.R.drawable.ic_menu_back)
    menu.add(NONE, 1, 1, R.string.menuOrderCreationFinish)
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
        startActivity(new Intent(this, classOf[Main]))
      //Toast.makeText(getApplicationContext(), "Apie ...", Toast.LENGTH_LONG).show()
      // TO DO padaryti apk'o mini aprašėlį
      case 1 =>
        Log.v(tagclass, " onMenuItemSelected case menuOrderCreationFinish ...")
        temp2order(getApplicationContext, FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName))
        startActivity(new Intent(this, classOf[Main]) /*.putExtra("createOrder", "yes")*/)
      case _ =>
    }
    true
  }

  def onClick(view: View) {
    newTemplateClickHandler(view)
    editClickHandler(view)
    //editClickTextViewHandler(view)
    deleteClickHandler(view)
  }

  def newTemplateClickHandler(v: View) {
    Log.v(tagclass + " newTemplateClickHandler", " ...")
    startActivity(new Intent(this, classOf[TemplateMaking]).
      putExtra("ned", "new").
      putExtra("groupId", getIntent.getStringExtra("groupId")))
  }

  def editClickHandler(v: View) {
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val matterCurrent = vwParentRow.getChildAt(1).asInstanceOf[TextView].getText.toString
   getThingByMatter(R.raw.thingsxml, "thingsxml.txt",
     matterCurrent, this.asInstanceOf[Templates]) match {
      case m if m.length == 0 =>
        Toast.makeText(/*getApplicationContext*/this,
          this.getResources().getString(R.string.errTemplateCreationFailed), Toast.LENGTH_LONG).show()
      case m =>
        startActivity(new Intent(this, classOf[TemplateMaking]).
          putExtra("ned", "edit").
          putExtra("matterCurrent", matterCurrent).
          putExtra("groupId", getIntent.getStringExtra("groupId")).
          putExtra("thingStringCurrent", m))
    }
  }

  def deleteClickHandler(v: View) {
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val matterCurrent = vwParentRow.getChildAt(1).asInstanceOf[TextView].getText.toString
    getThingByMatter(R.raw.thingsxml, "thingsxml.txt",
      matterCurrent, this.asInstanceOf[Templates]) match {
      case m if m.length == 0 =>
        Toast.makeText(this,
          this.getResources().getString(R.string.errTemplateDeletionFailed), Toast.LENGTH_LONG).show()
      case m =>
        val xmlStr: String = getXmlAsStr(R.raw.thingsxml, "thingsxml.txt", this.asInstanceOf[Templates])
        /*startActivity(new Intent(this, classOf[Templates/*TemplateMaking*/]).
          putExtra("ned", "delete").
          putExtra("matterCurrent", matterCurrent).
          putExtra("groupId", getIntent.getStringExtra("groupId")).
          putExtra("thingStringCurrent", m))*/
        // išmesti buvusį ruošinį
        val thingsNodeChanged: Node = removeTemplateByMatter(xmlStr, matterCurrent)
        Log.v(tagclass + "deleteClickHandler thingsNodeChanged", thingsNodeChanged.toString)
        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
        XML.save(dir + "/thingsxml.txt", thingsNodeChanged, "UTF-8", true, null)
        Toast.makeText(this, this.getResources().getString(R.string.infoTemplateIsDeleted), Toast.LENGTH_LONG).show()
        startActivity(new Intent(this, classOf[Templates]).
          putExtra("groupId", getIntent.getStringExtra("groupId")))
        finish

    }
  }

  def clickTextViewHandler(v: View) {
    this.getListView.setBackgroundColor(Color.BLACK) // GREEN
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val txtChild = vwParentRow.getChildAt(1).asInstanceOf[TextView]
    txtChild.setTextColor(Color.BLUE)

    val groupName: String = txtChild.asInstanceOf[TextView].getText.toString
    Log.v(tagclass + " clickTextViewHandler  groupName ...", groupName)
    val groupId: String = getTemplateGroupIdByName(R.raw.metadataxml, "metadataxml.txt", "no_extra",
      this.asInstanceOf[Groups], groupName)
    startActivity(new Intent(this, classOf[Templates]).
      putExtra("groupId", groupId))
  }



  def fillData() {
    //-> http://stackoverflow.com/questions/7318765/adding-button-to-each-row-in-listview
    //-> http://androidforbeginners.blogspot.com/2010/03/clicking-buttons-in-listview-row.html
    //-> http://eureka.ykyuen.info/2010/01/03/android-simple-listview-using-simpleadapter/
    //-> http://commonsware.com/Android/excerpt.pdf ?
    //var lv: ListView = findViewById(R.id.tvViewRow).asInstanceOf[ListView]
    val from = Array[String]("rowid")
    val to = Array[Int](R.id.tvTemplateViewRow)
    var fillMaps: List[HashMap[String, String]] = new ArrayList[HashMap[String, String]]

    val xmlStr: String =
      getXmlAsStr(R.raw.thingsxml, "thingsxml.txt", "orderString", this.asInstanceOf[Activity])
    Log.v(TAG + " Templates fillData xmlStr = ", xmlStr.toString)
    Log.v(TAG + " Templates fillData getStringExtra(\"groupId\") = ", getIntent.getStringExtra("groupId"))

    templateItems(xmlStr, getIntent.getStringExtra("groupId")).toArray[String].foreach(arg => {
      val map: HashMap[String, String] = new HashMap[String, String]
      map.put("rowid", arg)
      fillMaps.add(map)
    })
    Log.v(tagclass + " fillData fillMaps size ", fillMaps.size.toString)
    Log.v(tagclass + " fillData fillMaps ", fillMaps.toString)
    Log.v(tagclass + " fillData preorder_view_row_x ", templates_view_row_x.toString)
    this.setListAdapter(new SimpleAdapter(this, fillMaps, templates_view_row_x, from, to))
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
    logExtras(extras, msg)
  }


}

