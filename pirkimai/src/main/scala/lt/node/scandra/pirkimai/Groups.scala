package lt.node.scandra.pirkimai

import android.os.Bundle

import android.util.Log
import android.view.View.OnClickListener
import android.view.Menu._
import android.content.{Context, Intent}
import android.view._
import android.graphics.Color
import java.util.{List, ArrayList, HashMap}
import android.widget._

//import util.OrderPurchase._

import util.{FileUtil, FindView, ScalaFunPro, OrderPurchase}
import xml._
import transform.{RuleTransformer, RewriteRule}
import android.app.ListActivity

class Groups extends ListActivity with OnClickListener with OrderPurchase with ScalaFunPro /* with FindView*/ {
  // TO-done-DO padaryti grupės vardo keitimą
  // TO-done-DO padaryti TUŠČIOs grupės vardo trynimą
  // TO-done-DO padaryti naujos grupės sukūrimą

  //private[this] var templates_view_row_x: Int = R.layout.templates_view_row

  private[this] var context: Context = _

  /*
  //  override def onConfigurationChanged(newConfig: Configuration) {
  //    super.onConfigurationChanged(newConfig);
  //    templates_view_row_x = newConfig.orientation match {
  //      case Configuration.ORIENTATION_LANDSCAPE =>
  //        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
  //        R.layout.templates_view_row_land
  //      case Configuration.ORIENTATION_PORTRAIT =>
  //        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
  //        R.layout.templates_view_row
  //      case _ =>
  //        Toast.makeText(this, "NOT {landscape portrait} !!!", Toast.LENGTH_LONG).show()
  //        R.layout.templates_view_row
  //    }
  //    fillData()
  //  }
  */

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    //logExtras("onCreate ------ ")
    context = this.getApplicationContext

    getIntent match {
      case intent if intent.hasExtra("new") => {
        Log.v(TAG + " Groups", " case hasExtra(\"new\") ...")
        inputString(/*"Pavadinimas"*/ null, /*"Pranešimas"*/ null, okAction4New, cancelAction4New)

        def okAction4New(text: String) {
          Log.v(TAG + " Groups okAction", text)
          val meta: Node =
            XML.loadString(getXmlAsStr(R.raw.metadataxml, "metadataxml.txt", "no_extra", this.asInstanceOf[Groups]))
          val groups: scala.collection.immutable.List[String] =
            getTemplateGroups(R.raw.metadataxml, "metadataxml.txt", "no_extra", this.asInstanceOf[Groups]).toList
          groups.forall(s => (s != null) && (s != text) && (s.trim != "")) match {
            case true =>
              val maxId: Int = groups.map(s => getTemplateGroupIdByName(R.raw.metadataxml, "metadataxml.txt", "no_extra",
                this.asInstanceOf[Groups], s).toInt).max
              Log.v(TAG + " Groups okAction maxId", maxId.toString)
              val groupsNode: Node = ((meta \ "groups")(0))
              addChild(groupsNode, <v k={(maxId + 1).toString}>
                {text}
              </v>) match {
                case Some(groupsNodeUpdated) =>
                  val metaUpdated: Node = <meta>
                    {groupsNodeUpdated}{(meta \ "mandatProps")(0)}{(meta \ "optionProps")(0)}{(meta \ "optionMultiProps")(0)}{(meta \ "textProps")(0)}
                  </meta>
                  Log.v(TAG + " Groups okAction metaUpdated", metaUpdated.toString)
                  val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
                  XML.save(dir + "/metadataxml.txt", metaUpdated, "UTF-8", true, null)
                case None =>
                  Toast.makeText(context, "Can only add children to elements when 'cut'!", Toast.LENGTH_LONG).show()
              }
            case _ =>
              Toast.makeText(this, "KLAIDA: toks ruošinių grupės vardas jau yra ! ==>" + text, Toast.LENGTH_LONG).show()
          }
          startActivity(new Intent(this, classOf[Groups]).putExtra("case", "CrEdDe"))
        }

        def cancelAction4New() {
          Log.v(TAG + " Groups cancelAction", "")
          Toast.makeText(this, "Jūs atsisakėte naujos ruošinių grupės kūrimo", Toast.LENGTH_LONG).show()
          startActivity(new Intent(this, classOf[Groups]).putExtra("case", "CrEdDe"))
        }
      }

      case intent if intent.hasExtra("edit") => {
        val currentName: String = intent.getStringExtra("edit")
        Log.v(TAG + " Groups case hasExtra(\"edit\") currentName", currentName)
        val id: String = getTemplateGroupIdByName(R.raw.metadataxml, "metadataxml.txt", "no_extra", this.asInstanceOf[Groups], currentName)
        Log.v(TAG + " Groups case hasExtra(\"edit\") id", id)
        inputString(/*"Pavadinimas"*/ null, currentName, okAction4Edit, cancelAction4Edit)

        def okAction4Edit(text: String) {
          Log.v(TAG + " Groups okAction4Edit", text)
          val meta: Node = XML.loadString(getXmlAsStr(R.raw.metadataxml, "metadataxml.txt", "no_extra", this.asInstanceOf[Groups]))
          val groups: scala.collection.immutable.List[String] =
            getTemplateGroups(R.raw.metadataxml, "metadataxml.txt", "no_extra", this.asInstanceOf[Groups]).toList
          text match {
            case "" =>
              Toast.makeText(this, "KLAIDA: neįvestas ruošinių grupės vardas !", Toast.LENGTH_LONG).show()
              startActivity(new Intent(this, classOf[Groups]))
            case newText if groups.contains(newText) =>
              Toast.makeText(this, "KLAIDA: toks ruošinių grupės vardas jau yra ! ==>" + text, Toast.LENGTH_LONG).show()
              startActivity(new Intent(this, classOf[Groups]))
            case newText =>
              val groupsNode: Node = ((meta \ "groups")(0))
              val removeIt: RewriteRule = new RewriteRule {
                override def transform(n: Node): NodeSeq = n match {
                  case e: Elem if (e \ "@k").text == id => NodeSeq.Empty
                  case e => e
                }
              }
              val groupsCut = (new RuleTransformer(removeIt).transform(groupsNode))(0)
              Log.v(TAG + " Groups okAction4Edit groupsCut", groupsCut.toString)
              addChild(groupsCut, <v k={id}>
                {newText}
              </v>) match {
                case Some(groupsUpdated) =>
                  val metaUpdated: Node = <meta>
                    {groupsUpdated}{(meta \ "mandatProps")(0)}{(meta \ "optionProps")(0)}{(meta \ "optionMultiProps")(0)}{(meta \ "textProps")(0)}
                  </meta>
                  Log.v(TAG + " Groups okAction4Edit metaUpdated", metaUpdated.toString)
                  val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
                  XML.save(dir + "/metadataxml.txt", metaUpdated, "UTF-8", true, null)
                  Toast.makeText(this, "Ruošinių grupės vardas pakeistas", Toast.LENGTH_LONG).show()
                case None =>
                  Toast.makeText(context, "Can only add children to elements when 'cut'!", Toast.LENGTH_LONG).show()
              }
          }
          startActivity(new Intent(this, classOf[Groups]).putExtra("case", "CrEdDe"))
        }

        def cancelAction4Edit() {
          Log.v(TAG + " Groups cancelAction", "")
          Toast.makeText(this, "Jūs atsisakėte ruošinių grupės vardo keitimo", Toast.LENGTH_LONG).show()
          startActivity(new Intent(this, classOf[Groups]).putExtra("case", "CrEdDe"))
        }
      }

      case intent if intent.hasExtra("delete") => {
        val currentName: String = intent.getStringExtra("delete")
        Log.v(TAG + " Groups case hasExtra(\"delete\") currentName", currentName)
        val id: String = getTemplateGroupIdByName(R.raw.metadataxml, "metadataxml.txt", "no_extra", this.asInstanceOf[Groups], currentName)
        Log.v(TAG + " Groups case hasExtra(\"edit\") id", id)
        val meta: Node = XML.loadString(getXmlAsStr(R.raw.metadataxml, "metadataxml.txt", "no_extra", this.asInstanceOf[Groups]))
        val xmlStr: String =
          getXmlAsStr(R.raw.thingsxml, "thingsxml.txt", /* "orderString"*/ "no-extra", this.asInstanceOf[Groups])
        templateItems(xmlStr, id).length match {
          case size if size == 0 =>
            val groupsNode: Node = ((meta \ "groups")(0))
            val removeIt: RewriteRule = new RewriteRule {
              override def transform(n: Node): NodeSeq = n match {
                case e: Elem if (e \ "@k").text == id => NodeSeq.Empty
                case e => e
              }
            }
            val groupsCut = (new RuleTransformer(removeIt).transform(groupsNode))(0)
            Log.v(TAG + " Groups okAction4Delete groupsCut", groupsCut.toString)
            val metaUpdated: Node = <meta>
              {groupsCut}{(meta \ "mandatProps")(0)}{(meta \ "optionProps")(0)}{(meta \ "optionMultiProps")(0)}{(meta \ "textProps")(0)}
            </meta>
            Log.v(TAG + " Groups okAction4Delete metaUpdated", metaUpdated.toString)
            val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
            XML.save(dir + "/metadataxml.txt", metaUpdated, "UTF-8", true, null)
            Toast.makeText(this, "Ruošinių grupė ištrinta !", Toast.LENGTH_LONG).show()
          case size =>
            Toast.makeText(this, "KLAIDA: ruošinių grupė ne tuščia - trinti nevalia !", Toast.LENGTH_LONG).show()
        }
        startActivity(new Intent(this, classOf[Groups]).putExtra("case", "CrEdDe"))
      }

      case _ =>
        setContentView(R.layout.groups)
        getIntent.hasExtra("case") match {
          //case true if getIntent.getStringExtra("case") == "CrEdDe" =>
          //  this.setListAdapter(new SimpleAdapter(this, fillMaps, R.layout.groups_view_row, from, to))
          case true if getIntent.getStringExtra("case") == "Order" =>
          case _ =>
            val footerView: View = getLayoutInflater.inflate(R.layout.group_list_footer,
              findViewById(R.id.groups_footer_layout).asInstanceOf[ViewGroup])
            getListView.addFooterView(footerView, null, true)
        }
        /*val footerView: View = getLayoutInflater.inflate(R.layout.group_list_footer,
          findViewById(R.id.groups_footer_layout).asInstanceOf[ViewGroup])
        getListView.addFooterView(footerView, null, true)*/
        fillData()
    }

  }

  override def onCreateOptionsMenu(menu: Menu) = {
    super.onCreateOptionsMenu(menu)
    menu.add(NONE, 0, 0, R.string.home) //.setIcon(android.R.drawable.ic_menu_back)
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
    newGroupClickHandler(view)
    editClickHandler(view)
    deleteClickHandler(view)
    clickTextViewHandler(view)
    clickTextViewHandler4Order(view)
  }

  def newGroupClickHandler(v: View) {
    startActivity(new Intent(this, classOf[Groups]).putExtra("new", "yes"))
  }

  def editClickHandler(v: View) {
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val txtChild = vwParentRow.getChildAt(1).asInstanceOf[TextView]
    startActivity(new Intent(this, classOf[Groups]).putExtra("edit", txtChild.asInstanceOf[TextView].getText.toString))
  }

  def deleteClickHandler(v: View) {
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val txtChild = vwParentRow.getChildAt(1).asInstanceOf[TextView]
    startActivity(new Intent(this, classOf[Groups]).putExtra("delete", txtChild.asInstanceOf[TextView].getText.toString))
  }

  def clickTextViewHandler(v: View) {
    this.getListView.setBackgroundColor(Color.BLACK) // GREEN
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val txtChild = vwParentRow.getChildAt(1).asInstanceOf[TextView]
    txtChild.setTextColor(Color.BLUE)

    val groupName: String = txtChild.asInstanceOf[TextView].getText.toString
    Log.v(this.TAG + " Groups clickTextViewHandler  groupName ...", groupName)
    val groupId: String = getTemplateGroupIdByName(R.raw.metadataxml, "metadataxml.txt", "no_extra",
      this.asInstanceOf[Groups], groupName)
    startActivity(new Intent(this, classOf[Templates]).putExtra("groupId", groupId))
  }

  def clickTextViewHandler4Order(v: View) {
    this.getListView.setBackgroundColor(Color.BLACK) // GREEN
    val vwParentRow: LinearLayout = v.getParent.asInstanceOf[LinearLayout]
    val txtChild = vwParentRow.getChildAt(0).asInstanceOf[TextView]
    txtChild.setTextColor(Color.BLUE)

    val groupName: String = txtChild/*.asInstanceOf[TextView]*/.getText.toString
    Log.v(this.TAG + " Groups clickTextViewHandler  groupName ...", groupName)
    val groupId: String = getTemplateGroupIdByName(R.raw.metadataxml, "metadataxml.txt", "no_extra",
      this.asInstanceOf[Groups], groupName)
    startActivity(new Intent(this, classOf[/*Templates*/Order]).
      //putExtra("createOrder", "yes").
      putExtra("groupId", groupId))
  }


  def fillData() {
    //-> http://stackoverflow.com/questions/7318765/adding-button-to-each-row-in-listview
    //-> http://androidforbeginners.blogspot.com/2010/03/clicking-buttons-in-listview-row.html
    //-> http://eureka.ykyuen.info/2010/01/03/android-simple-listview-using-simpleadapter/
    //-> http://commonsware.com/Android/excerpt.pdf ?

    //    Log.v(this.TAG + " Groups fillData fillMaps getChildCount", this.getListView.getChildCount.toString)
    //    Log.v(this.TAG + " Groups fillData fillMaps getChildAt(0)", this.getListView.getChildAt(0).toString)
    //    Log.v(this.TAG + " Groups fillData fillMaps getChildAt(1)", this.getListView.getChildAt(1).toString)
    //    Log.v(this.TAG + " Groups fillData fillMaps getChildAt(2)", this.getListView.getChildAt(2).toString)
    //    getIntent.hasExtra("case") match {
    //      case true if getIntent.getStringExtra("case") == "CrEdDe" =>
    //        this.getListView.getChildAt(0).asInstanceOf[ImageButton].setVisibility(View.VISIBLE)
    //        this.getListView.getChildAt(2).asInstanceOf[ImageButton].setVisibility(View.GONE)
    //      case _ =>
    //        this.getListView.getChildAt(0).asInstanceOf[ImageButton].setVisibility(View.GONE)
    //        this.getListView.getChildAt(2).asInstanceOf[ImageButton].setVisibility(View.GONE)
    //    }

    val from = Array[String]("rowid")
    val to = Array[Int](R.id.tvGroupViewRow)
    var fillMaps: List[HashMap[String, String]] = new ArrayList[HashMap[String, String]]

    val groups: Array[String] =
      getTemplateGroups(R.raw.metadataxml, "metadataxml.txt", "no_extra", this.asInstanceOf[Groups])
    Log.v(TAG + " Groups fillData groups", groups.toString)
    groups.foreach(arg => {
      Log.v(this.TAG + " Groups fillData arg", arg)
      val map: HashMap[String, String] = new HashMap[String, String]
      map.put("rowid", arg)
      fillMaps.add(map)
    })
    Log.v(this.TAG + " Groups fillData fillMaps size", fillMaps.size.toString)
    Log.v(this.TAG + " Groups fillData fillMaps", fillMaps.toString)
    Log.v(this.TAG + " Groups fillData groups_view_row", R.layout.groups_view_row.toString)

    getIntent.hasExtra("case") match {
      //case true if getIntent.getStringExtra("case") == "CrEdDe" =>
      //  this.setListAdapter(new SimpleAdapter(this, fillMaps, R.layout.groups_view_row, from, to))
      case true if getIntent.getStringExtra("case") == "Order" =>
        this.setListAdapter(new SimpleAdapter(this, fillMaps, R.layout.groups_view_row_4templ, from, to))
      case _ =>
        this.setListAdapter(new SimpleAdapter(this, fillMaps, R.layout.groups_view_row, from, to))
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

