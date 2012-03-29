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

import android.app.ListActivity
import xml.{Null, UnprefixedAttribute, XML}

class TemplateAttrib extends ListActivity with OnClickListener with OrderPurchase with ScalaFunPro {

  private[this] var etMatter: EditText = _
  private[this] var spinMeasure: Spinner = _
  private[this] var bntCreate: Button = _
  private[this] var bntContinue: Button = _
  private[this] var attrib_list_row_x: Int = R.layout.attrib_list_row
  //  private[this] var templates_view_row_x: Int = R.layout.templates_view_row

  //  private[this] var btn: Button = _
  private[this] var context: Context = _
  //  private[this] var thingsxmlFile: String = _
  private[this] var attribValues: Array[String] = Array()

  override def onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig);
    attrib_list_row_x = newConfig.orientation match {
      case Configuration.ORIENTATION_LANDSCAPE =>
        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        R.layout.attrib_list_row //templates_view_row_land
      case Configuration.ORIENTATION_PORTRAIT =>
        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        R.layout.attrib_list_row
      case _ =>
        Toast.makeText(this, "NOT {landscape portrait} !!!", Toast.LENGTH_LONG).show()
        R.layout.attrib_list_row
    }
    fillData()
  }

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    context = this.getApplicationContext
    logExtras("onCreate--- ")

    setContentView(R.layout.attribs)
    val headerView: View =
      getLayoutInflater.inflate(R.layout.attrib_list_header,
        findViewById(R.id.attrib_header_layout).asInstanceOf[ViewGroup])
    getListView.addHeaderView(headerView, null, true)
    val footerView: View =
      getLayoutInflater.inflate(R.layout.attrib_list_footer,
        findViewById(R.id.attrib_footer_layout).asInstanceOf[ViewGroup])
    getListView.addFooterView(footerView, null, true)
    fillData()

  }

  override def onCreateOptionsMenu(menu: Menu) = {
    super.onCreateOptionsMenu(menu)
    menu.add(NONE, 0, 0, R.string.home) //.setIcon(android.R.drawable.ic_menu_back)
    //menu.add(NONE, 1, 1, R.string.menuOrderCreationFinish)
    true
  }

  override def onMenuItemSelected(featureId: Int, item: MenuItem) = {
    super.onMenuItemSelected(featureId, item)
    item.getItemId match {
      case 0 =>
        startActivity(new Intent(this, classOf[Main]))
      case _ =>
    }
    true
  }

  def onClick(view: View) {
    newValueClickHandler(view)
    editValueClickHandler(view)
    delValueClickHandler(view)
    deleteAttribClickHandler(view)
    createAttribClickHandler(view)
  }

  def newValueClickHandler(v: View) {
    Log.v(tagclass, "newValueClickHandler ...")
    inputString(this.getResources.getString(R.string.NEW_template_attrib_value), null, okNew, cancelNew)

    def okNew(text: String) {
      Log.v(tagclass + "okAction", text)
      text match {
        case s if s.trim.length > 0 =>
          attribValues = attribValues :+ text
        case s =>
          Toast.makeText(this, this.getResources.getString(R.string.errZeroLengthString), Toast.LENGTH_LONG).show()
      }
      val i = new Intent(this, classOf[TemplateAttrib]).
        //startActivity(new Intent(this, classOf[TemplateAttrib]).
        putExtra("ned", getIntent.getStringExtra("ned")).
        putExtra("attrib", getIntent.getStringExtra("attrib")).
        putExtra("groupId", getIntent.getStringExtra("groupId")).
        putExtra("thingString", getIntent.getStringExtra("thingString")).
        putExtra("attribValues", attribValues)
      if (getIntent.getStringExtra("ned") == "edit") {
        i.putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
          putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent"))
      }
      startActivity(i)
    }

    def cancelNew() {
      Log.v(tagclass + "cancelNew", "")
      Toast.makeText(this, this.getResources.getString(R.string.infoRejectTheAction), Toast.LENGTH_LONG).show()
      val i = new Intent(this, classOf[TemplateAttrib]).
        //startActivity(new Intent(this, classOf[TemplateAttrib]).
        putExtra("ned", getIntent.getStringExtra("ned")).
        putExtra("attrib", getIntent.getStringExtra("attrib")).
        putExtra("groupId", getIntent.getStringExtra("groupId")).
        putExtra("thingString", getIntent.getStringExtra("thingString")).
        putExtra("attribValues", attribValues)
      if (getIntent.getStringExtra("ned") == "edit") {
        i.putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
          putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent"))
      }
      startActivity(i)
    }
  }

  def editValueClickHandler(v: View) {
    Log.v(tagclass, "editValueClickHandler ...")
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val oldAttribValue = vwParentRow.getChildAt(0).asInstanceOf[TextView].getText.toString
    inputString(this.getResources.getString(R.string.NEW_template_attrib_value), oldAttribValue, okEdit, cancelEdit)

    def okEdit(text: String) {
      Log.v(tagclass + "okEdit", text)
      text match {
        case s if s.trim.length > 0 =>
          attribValues = attribValues.filter(_ != oldAttribValue) :+ text
        case s =>
          Toast.makeText(this, this.getResources.getString(R.string.errZeroLengthString), Toast.LENGTH_LONG).show()
      }
      val i = new Intent(this, classOf[TemplateAttrib]).
        //startActivity(new Intent(this, classOf[TemplateAttrib]).
        putExtra("ned", getIntent.getStringExtra("ned")).
        putExtra("attrib", getIntent.getStringExtra("attrib")).
        putExtra("groupId", getIntent.getStringExtra("groupId")).
        putExtra("thingString", getIntent.getStringExtra("thingString")).
        putExtra("attribValues", attribValues)
      if (getIntent.getStringExtra("ned") == "edit") {
        i.putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
          putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent"))
      }
      startActivity(i)
    }

    def cancelEdit() {
      Log.v(tagclass + "cancelEdit", "")
      Toast.makeText(this, this.getResources.getString(R.string.infoRejectTheAction), Toast.LENGTH_LONG).show()
      val i = new Intent(this, classOf[TemplateAttrib]).
        //startActivity(new Intent(this, classOf[TemplateAttrib]).
        putExtra("ned", getIntent.getStringExtra("ned")).
        putExtra("attrib", getIntent.getStringExtra("attrib")).
        putExtra("groupId", getIntent.getStringExtra("groupId")).
        putExtra("thingString", getIntent.getStringExtra("thingString")).
        putExtra("attribValues", attribValues)
      if (getIntent.getStringExtra("ned") == "edit") {
        i.putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
          putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent"))
      }
      startActivity(i)
    }
  }

  def delValueClickHandler(v: View) {
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val oldAttribValue = vwParentRow.getChildAt(0).asInstanceOf[TextView].getText.toString
    attribValues = attribValues.filter(_ != oldAttribValue)
    val i = new Intent(this, classOf[TemplateAttrib]).
      //startActivity(new Intent(this, classOf[TemplateAttrib]).
      putExtra("ned", getIntent.getStringExtra("ned")).
      putExtra("attrib", getIntent.getStringExtra("attrib")).
      putExtra("groupId", getIntent.getStringExtra("groupId")).
      putExtra("thingString", getIntent.getStringExtra("thingString")).
      putExtra("attribValues", attribValues)
    if (getIntent.getStringExtra("ned") == "edit") {
      i.putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
        putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent"))
    }
    startActivity(i)
  }

  def createAttribClickHandler(v: View) {
    val attribValuesStr: String = /*(Array("-") ++*/ attribValues /*++ Array {"..."})*/.mkString("|")
    Log.v(tagclass + "createAttribClickHandler ned", getIntent.getStringExtra("ned"))
    Log.v(tagclass + "createAttribClickHandler addAttrib", getIntent.getStringExtra("attrib"))
    Log.v(tagclass + "createAttribClickHandler value", attribValuesStr)
    Log.v(tagclass + "createAttribClickHandler thingString old", getIntent.getStringExtra("thingString"))
    val thingString: String = (XML.loadString(getIntent.getStringExtra("thingString")) %
      new UnprefixedAttribute(getIntent.getStringExtra("attrib"), attribValuesStr, Null)).toString
    Log.v(tagclass + "createAttribClickHandler thingString new", thingString)
    val i = new Intent(this, classOf[TemplateMaking]).
      //startActivity(new Intent(this, classOf[TemplateMaking]).
      putExtra("ned", getIntent.getStringExtra("ned")).
      putExtra("attrib", getIntent.getStringExtra("attrib")).
      putExtra("groupId", getIntent.getStringExtra("groupId")).
      putExtra("thingString", thingString)
    if (getIntent.getStringExtra("ned") == "edit") {
      i.putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
        putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent"))
    }
    startActivity(i)
    finish()
  }

  def deleteAttribClickHandler(v: View) {
    val thingString: String =
      removeOrderItemAttrib(getIntent.getStringExtra("thingString"), getIntent.getStringExtra("attrib")).toString
    val i = new Intent(this, classOf[TemplateMaking]).
      //startActivity(new Intent(this, classOf[TemplateMaking]).
      putExtra("ned", getIntent.getStringExtra("ned")).
      putExtra("attrib", getIntent.getStringExtra("attrib")).
      putExtra("groupId", getIntent.getStringExtra("groupId")).
      putExtra("thingString", thingString)
    if (getIntent.getStringExtra("ned") == "edit") {
      i.putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
        putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent"))
    }
    startActivity(i)
    finish()
  }

  def fillData() {
    //-> http://stackoverflow.com/questions/7318765/adding-button-to-each-row-in-listview
    //-> http://androidforbeginners.blogspot.com/2010/03/clicking-buttons-in-listview-row.html
    //-> http://eureka.ykyuen.info/2010/01/03/android-simple-listview-using-simpleadapter/
    //-> http://commonsware.com/Android/excerpt.pdf ?

    //var lv: ListView = findViewById(R.id.tvViewRow).asInstanceOf[ListView]
    attribValues = getIntent match {
      case intent if intent.hasExtra("attribValues") =>
        Log.v(TAG + this.getClass.getSimpleName + "case hasExtra attribValues", "")
        intent.getStringArrayExtra("attribValues")
      case _ =>
        getIntent.getStringExtra("attrib") match {
          case attr if attr == "qty" => getQtys(this.asInstanceOf[TemplateAttrib])
          case attr if attr == "name" => Array[String]()
          case attr if attr == "rate" =>
            Log.v(TAG + this.getClass.getName + " fillData values rate ", getXxxxs(attr, this.asInstanceOf[TemplateAttrib]).toString)
            getXxxxs(attr, this.asInstanceOf[TemplateAttrib])
          case attr if attr == "fatness" => getXxxxs(attr, this.asInstanceOf[TemplateAttrib])
          case attr if attr == "vol" => getXxxxs(attr, this.asInstanceOf[TemplateAttrib])
          case attr if attr == "kind" => Array[String]()
          case attr => Array[String]()
        }
    }

    val from = Array[String]("rowid")
    val to = Array[Int](R.id.tvAttribViewRow)
    var fillMaps: List[HashMap[String, String]] = new ArrayList[HashMap[String, String]]

    attribValues.foreach(arg => {
      val map: HashMap[String, String] = new HashMap[String, String]
      map.put("rowid", arg)
      fillMaps.add(map)
    })
    //Log.v(TAG + this.getClass.getName, " case: new & thingString ...")
    Log.v(tagclass + "fillData fillMaps size ", fillMaps.size.toString)
    Log.v(tagclass + "fillData fillMaps ", fillMaps.toString)
    Log.v(tagclass + "fillData attrib_list_row_x ", attrib_list_row_x.toString)
    this.setListAdapter(new SimpleAdapter(this, fillMaps, attrib_list_row_x, from, to))
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

