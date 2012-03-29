package lt.node.scandra.pirkimai

import android.app.Activity
import android.os.Bundle
import android.view.View._
import android.util.Log
import android.widget._
import util.FindView._
import util.{FileUtil, ScalaFunPro, FindView, OrderPurchase}
import xml._
import android.content.{Context, Intent}
import android.content.res.Configuration
import transform.{RuleTransformer, RewriteRule}
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import android.graphics.Color


class TemplateMaking extends Activity /* with OnClickListener*/ with OrderPurchase with ScalaFunPro with FindView {
  // TO-done-DO padaryti naujo ruošinio sukūrimą
  // TODO padaryti ruošinio keitimą
  // TODO padaryti ruošinio trynimą su papildomu klausimu

  private[this] var etMatter: EditText = _
  private[this] var spinMeasure: Spinner = _
  private[this] var btnQty, btnRate, btnFatness, btnVol, btnName: Button = _
  private[this] var btnKind: Button = _
  private[this] var templates_view_row_x: Int = R.layout.templates_view_row

  private[this] var context: Context = _

  override def onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig);
    templates_view_row_x = newConfig.orientation match {
      case Configuration.ORIENTATION_LANDSCAPE =>
        //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        R.layout.templates_view_row_land
      case Configuration.ORIENTATION_PORTRAIT =>
        //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        R.layout.templates_view_row
      case _ =>
        //Toast.makeText(this, "NOT {landscape portrait} !!!", Toast.LENGTH_LONG).show()
        R.layout.templates_view_row
    }
    //fillData()
  }

  //  private[this] var orderItemText: TextView = _
  //  private[this] var btnQty, btnRate, btnFatness, btnVol, btnName: Button = _
  //  private[this] var btnKind: Button = _
  //  // TODO -- private[this] var kind: SelectMany = _
  //  private[this] var btnNote: Button = _
  //  private[this] var btnOK, btnCANCEL: Button = _
  //  //private[this] var btnCANCEL: Button = _
  //
  //  private[this] var thingNodeXmlString: String = _
  //  private[this] var orderNodeXmlString: String = _
  //  private[this] var origOrderNodeXmlString: String = _
  //  private[this] var attribName: String = _
  //  private[this] var attribValue: String = _

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    context = this.getApplicationContext
    logExtras("onCreate=== ")
    val templateGroupId = getIntent.getStringExtra("groupId")

    getIntent match {
      case intent if intent.hasExtra("ned") && intent.getStringExtra("ned") == "new" => {
        // kuriamas naujas ruošinys
        Log.v(tagclass, " case: new ...")
        val xmlStr: String = getXmlAsStr(R.raw.thingsxml, "thingsxml.txt", /*"thingString", */ this.asInstanceOf[TemplateMaking])
        getIntent match {
          case intent if !(intent.hasExtra("thingString")) => {
            // ruošinys dar nesukurtas
            setContentView(R.layout.template_new)
            Log.v(tagclass, " case: new & !thingString ...")

            etMatter = findViewById(R.id.matter).asInstanceOf[EditText]
            spinMeasure = findViewById(R.id.measure).asInstanceOf[Spinner]
            Log.v(tagclass + "onCreate spinMeasure", spinMeasure.toString)
            setSpinMeasure()

            findView[Button](R.id.btnCREATE).onClick {
              view: View => // bus kuriamas minimalus ruošinys
                val matter = etMatter.getText.toString
                isUniqueMatterName(xmlStr, matter) match {
                  case true =>
                    val unit = spinMeasure.getSelectedItem.asInstanceOf[String]
                    val thingElemStr: String = initTemplate(matter, unit, templateGroupId)
                    Toast.makeText(this, "btnCREATE " + thingElemStr, Toast.LENGTH_LONG).show()
                    // TO-done-DO pridėti naują ruošinį failan "thingsxml.txt"
                    addChild(XML.loadString(xmlStr), XML.loadString(thingElemStr)) match {
                      case Some(thingsUpdated) =>
                        Log.v(tagclass + "TemplateMaking new thingsUpdated", thingsUpdated.toString)
                        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
                        XML.save(dir + "/thingsxml.txt", thingsUpdated, "UTF-8", true, null)
                      case None =>
                        Toast.makeText(this, this.getResources().getString(R.string.errTemplateCreationFailed), Toast.LENGTH_LONG).show()
                    }
                  case false =>
                    Toast.makeText(this, this.getResources().getString(R.string.errTemplateIsntUnique), Toast.LENGTH_LONG).show()
                }
                startActivity(new Intent(this, classOf[Templates]).putExtra("groupId", templateGroupId))
            }

            findView[Button](R.id.btnCONTINUE).onClick {
              view: View => // bus pridedama atributų
                val matter = etMatter.getText.toString
                isUniqueMatterName(xmlStr, matter) match {
                  case true =>
                    val unit = spinMeasure.getSelectedItem.asInstanceOf[String]
                    val thingElemStr: String = initTemplate(matter, unit, templateGroupId)
                    Toast.makeText(this, "btnCONTINUE " + thingElemStr, Toast.LENGTH_LONG).show()
                    startActivity(new Intent(this, classOf[TemplateMaking]).
                      putExtra("ned", "new").
                      putExtra("groupId", templateGroupId).
                      putExtra("thingString", thingElemStr))
                  case false =>
                    Toast.makeText(context, "KLAIDA: Ruošinys tokiu vardu jau yra !", Toast.LENGTH_LONG).show()
                    startActivity(new Intent(this, classOf[Templates]).putExtra("groupId", templateGroupId))
                }
            }

            def setSpinMeasure() {
              //if (spinMeasure == null) spinMeasure = findViewById(R.id.measure).asInstanceOf[Spinner]
              val measures: Array[String] = getMeasures(R.raw.metadataxml, "metadataxml.txt", /*"no_extra",*/ this.asInstanceOf[TemplateMaking])
              Log.v(tagclass + "setSpinMeasure measures", measures.toString)
              val units = new ArrayAdapter[String](this, R.layout.template_new_spinner_view, measures)
              Log.v(tagclass + "setSpinMeasure units", units.toString)
              Log.v(tagclass + "setSpinMeasure spinMeasure", spinMeasure.toString)
              spinMeasure.setAdapter(units)
              Log.v(tagclass + "setSpinMeasure ...[]", "")
            }
          }

          case intent => {
            // jau naujas ruošinys yra pradėtas, bus pridedami atributai galbūt
            setContentView(R.layout.template_new_cont)
            val thingString: String = intent.getStringExtra("thingString")
            Log.v(tagclass + "case: new & thingString ...", " ...")
            Log.v(tagclass + "bus pridedami atributai", thingString)
            btnQty = findViewById(R.id.btnQty).asInstanceOf[Button]
            btnName = findViewById(R.id.btnName).asInstanceOf[Button]
            btnRate = findViewById(R.id.btnRate).asInstanceOf[Button]
            btnFatness = findViewById(R.id.btnFatness).asInstanceOf[Button]
            btnVol = findViewById(R.id.btnVol).asInstanceOf[Button]
            btnKind = findViewById(R.id.btnKind).asInstanceOf[Button]
            setBtnTxtColor(btnQty, "qty")
            setBtnTxtColor(btnName, "name")
            setBtnTxtColor(btnRate, "rate")
            setBtnTxtColor(btnFatness, "fatness")
            setBtnTxtColor(btnVol, "vol")
            setBtnTxtColor(btnKind, "kind")

            scala.collection.immutable.List[Tuple2[Int, String]](
              (R.id.btnQty, "qty"), (R.id.btnName, "name"),
              (R.id.btnRate, "rate"), (R.id.btnFatness, "fatness"),
              (R.id.btnVol, "vol") /*,(R.id.btnKind, "kind")*/).foreach(arg =>
              findView[Button](arg._1).onClick {
                view: View =>
                  startActivity(new Intent(this, classOf[TemplateAttrib]).
                    putExtra("ned", /*"new"*/ intent.getStringExtra("ned")).
                    putExtra("attrib", arg._2).
                    putExtra("groupId", templateGroupId).
                    putExtra("thingString", getIntent.getStringExtra("thingString")))
              }
            )

            findView[Button](R.id.btnCREATE).onClick {
              view: View =>
                val thingElemStr: String = getIntent.getStringExtra("thingString")
                addChild(XML.loadString(xmlStr), XML.loadString(thingElemStr)) match {
                  case Some(thingsUpdated) =>
                    Log.v(tagclass + "new thingsUpdated", thingsUpdated.toString)
                    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
                    XML.save(dir + "/thingsxml.txt", thingsUpdated, "UTF-8", true, null)
                    Toast.makeText(this, this.getResources().getString(R.string.infoTemplateIsCreated), Toast.LENGTH_LONG).show()
                  case None =>
                    Toast.makeText(context, this.getResources().getString(R.string.errTemplateCreationFailed), Toast.LENGTH_LONG).show()
                }
                startActivity(new Intent(this, classOf[Templates]).putExtra("groupId", templateGroupId))
            }

            def setBtnTxtColor(btnName: Button, attribName: String) {
              btnName.setTextColor(((XML.loadString(thingString) \ ("@" + attribName)).size == 0) match {
                case true => Color.RED
                case false => Color.GREEN
              })
            }
          }
        }

        def isUniqueMatterName(xmlStr: String, newMatter: String): Boolean = {
          val res0 = XML.loadString(xmlStr)
          !(res0 \\ "@matter").exists(_.text == newMatter)
        }
      }

      /*
startActivity(new Intent(this, classOf[TemplateMaking]).
     putExtra("ned", "edit").
     putExtra("matter", matter).
     putExtra("thingStringCurrent", m))*/
      case intent if intent.hasExtra("ned") && intent.getStringExtra("ned") == "edit" => {
        // keičiamas ruošinys
        Log.v(tagclass, " case: edit ...")
        val xmlStr: String = getXmlAsStr(R.raw.thingsxml, "thingsxml.txt", this.asInstanceOf[TemplateMaking])
        val matterCurrent: String = intent.getStringExtra("matterCurrent")
        getIntent match {
          case intent if !(intent.hasExtra("thingString")) => {
            // ruošinys dar nesukurtas
            setContentView(R.layout.template_new)
            Log.v(tagclass, " case: new & !thingString ...")
            val thingString: String = intent.getStringExtra("thingStringCurrent")

            etMatter = findViewById(R.id.matter).asInstanceOf[EditText]
            etMatter.setText(matterCurrent.toCharArray)

            spinMeasure = findViewById(R.id.measure).asInstanceOf[Spinner]
            setSpinMeasure((XML.loadString(thingString) \ "@measure").toString)
            //val measures: Array[String] = getMeasures(R.raw.metadataxml, "metadataxml.txt", this.asInstanceOf[TemplateMaking])
            ////measures.indexOf(matter)
            //spinMeasure.setSelection(measures.indexOf((XML.loadString(thingString)\"@measure").toString))

            findViewById(R.id.btnCREATE).asInstanceOf[Button].setText(R.string.btnOK)
            Log.v(tagclass + "onCreate edit spinMeasure", spinMeasure.toString)

            findView[Button](R.id.btnCREATE).onClick {
              view: View => // bus keičiamas minimalus ruošinys
                val matter = etMatter.getText.toString
                isUniqueMatterName(xmlStr, matter) match {
                  case true =>
                    val unit = spinMeasure.getSelectedItem.asInstanceOf[String]
                    val thingStringUpdated: String =
                      changeElemAttrib(changeElemAttrib(thingString, "matter", matter), "measure", unit)
                    Log.v(tagclass + "TemplateMaking edit thingStringUpdated", thingStringUpdated)
                    Toast.makeText(this, "btnCREATE " + thingStringUpdated, Toast.LENGTH_SHORT).show()
                    // išmesti buvusį ruošinį
                    val thingsNodeChanged: Node = removeTemplateByMatter(xmlStr, matterCurrent)
                    Log.v(tagclass + "TemplateMaking edit thingsNodeChanged", thingsNodeChanged.toString)
                    // pridėti pakeistą ruošinį
                    addChild(thingsNodeChanged, XML.loadString(thingStringUpdated)) match {
                      case Some(thingsUpdated) =>
                        Log.v(tagclass + "TemplateMaking edit thingsUpdated", thingsUpdated.toString)
                        val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
                        XML.save(dir + "/thingsxml.txt", thingsUpdated, "UTF-8", true, null)
                        Toast.makeText(this, this.getResources().getString(R.string.infoTemplateIsChanged), Toast.LENGTH_LONG).show()
                      case None =>
                        Toast.makeText(this, this.getResources().getString(R.string.errTemplateUpdateFailed), Toast.LENGTH_LONG).show()
                    }
                  case false =>
                    Toast.makeText(this, this.getResources().getString(R.string.errTemplateIsntUnique), Toast.LENGTH_LONG).show()
                }
                startActivity(new Intent(this, classOf[Templates]).putExtra("groupId", templateGroupId))
                finish
            }

            findView[Button](R.id.btnCONTINUE).onClick {
              view: View => // bus  galb8tpridedama atributų
                val matter = etMatter.getText.toString
                //isUniqueMatterName(xmlStr, matter) match {
                //  case true =>
                val unit = spinMeasure.getSelectedItem.asInstanceOf[String]
                val thingStringUpdated: String =
                  changeElemAttrib(changeElemAttrib(thingString, "matter", matter), "measure", unit)
                Toast.makeText(this, "btnCREATE " + thingStringUpdated, Toast.LENGTH_SHORT).show()
                startActivity(new Intent(this, classOf[TemplateMaking]).
                  putExtra("ned", "edit").
                  putExtra("groupId", templateGroupId).
                  putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
                  putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent")).
                  putExtra("thingString", thingStringUpdated))
              //  case false =>
              //    Toast.makeText(this, this.getResources().getString(R.string.errTemplateIsntUnique), Toast.LENGTH_LONG).show()
              //    startActivity(new Intent(this, classOf[Templates]).putExtra("groupId", templateGroupId))
              //}
            }

            def setSpinMeasure(unit: String) {
              //if (spinMeasure == null) spinMeasure = findViewById(R.id.measure).asInstanceOf[Spinner]
              val measures: Array[String] = getMeasures(R.raw.metadataxml, "metadataxml.txt", this.asInstanceOf[TemplateMaking])
              Log.v(tagclass + "setSpinMeasure measures", measures.toString)
              val units = new ArrayAdapter[String](this, R.layout.template_new_spinner_view, measures)
              Log.v(tagclass + "setSpinMeasure units", units.toString)
              Log.v(tagclass + "setSpinMeasure spinMeasure", spinMeasure.toString)
              spinMeasure.setAdapter(units)
              spinMeasure.setSelection(units.getPosition(unit))
              Log.v(tagclass + "edit setSpinMeasure ", <_>unit=
                {unit}
                ; position=
                {units.getPosition(unit)}
                ; ...[]</_>.text)
            }
          }

          case intent => {
            // bus keičiami / pridedami atributai galbūt
            setContentView(R.layout.template_new_cont)
            val thingString: String = intent.getStringExtra("thingString")
            Log.v(tagclass + "case: new & thingString ...", " ...")
            Log.v(tagclass + "bus pridedami atributai", thingString)
            btnQty = findViewById(R.id.btnQty).asInstanceOf[Button]
            btnName = findViewById(R.id.btnName).asInstanceOf[Button]
            btnRate = findViewById(R.id.btnRate).asInstanceOf[Button]
            btnFatness = findViewById(R.id.btnFatness).asInstanceOf[Button]
            btnVol = findViewById(R.id.btnVol).asInstanceOf[Button]
            btnKind = findViewById(R.id.btnKind).asInstanceOf[Button]
            setBtnTxtColor(btnQty, "qty")
            setBtnTxtColor(btnName, "name")
            setBtnTxtColor(btnRate, "rate")
            setBtnTxtColor(btnFatness, "fatness")
            setBtnTxtColor(btnVol, "vol")
            setBtnTxtColor(btnKind, "kind")

            scala.collection.immutable.List[Tuple2[Int, String]](
              (R.id.btnQty, "qty"), (R.id.btnName, "name"),
              (R.id.btnRate, "rate"), (R.id.btnFatness, "fatness"),
              (R.id.btnVol, "vol") /*,(R.id.btnKind, "kind")*/).foreach(arg =>
              findView[Button](arg._1).onClick {
                view: View =>
                  startActivity(new Intent(this, classOf[TemplateAttrib]).
                    putExtra("ned", intent.getStringExtra("ned")).
                    putExtra("attrib", arg._2).
                    putExtra("groupId", templateGroupId).
                    putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
                    putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent")).
                    putExtra("thingString", getIntent.getStringExtra("thingString")))
              }
            )

            findView[Button](R.id.btnCREATE).onClick {
              view: View =>
                val thingElemStr: String = getIntent.getStringExtra("thingString")

/*
startActivity(new Intent(this, classOf[TemplateMaking]).
putExtra("ned", "edit").
putExtra("groupId", templateGroupId).
putExtra("matterCurrent", getIntent.getStringExtra("matterCurrent")).
putExtra("thingStringCurrent", getIntent.getStringExtra("thingStringCurrent")).
putExtra("thingString", thingStringUpdated))
*/
                // išmesti buvusį ruošinį
                val thingsNodeChanged: Node = removeTemplateByMatter(xmlStr, matterCurrent)
                Log.v(tagclass + "TemplateMaking edit thingsNodeChanged 2", thingsNodeChanged.toString)
                // pridėti pakeistą ruošinį
                addChild(thingsNodeChanged, XML.loadString(getIntent.getStringExtra("thingString"))) match {
                  case Some(thingsUpdated) =>
                    Log.v(tagclass + "TemplateMaking edit thingsUpdated 2", thingsUpdated.toString)
                    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
                    XML.save(dir + "/thingsxml.txt", thingsUpdated, "UTF-8", true, null)
                    Toast.makeText(this, this.getResources().getString(R.string.infoTemplateIsChanged), Toast.LENGTH_LONG).show()
                  case None =>
                    Toast.makeText(this, this.getResources().getString(R.string.errTemplateUpdateFailed), Toast.LENGTH_LONG).show()
                }
                /*addChild(XML.loadString(xmlStr), XML.loadString(thingElemStr)) match {
                  case Some(thingsUpdated) =>
                    Log.v(tagclass + "new thingsUpdated", thingsUpdated.toString)
                    val dir = FileUtil.getExternalFilesDirAllApiLevels(this.getPackageName);
                    XML.save(dir + "/thingsxml.txt", thingsUpdated, "UTF-8", true, null)
                    Toast.makeText(this, this.getResources().getString(R.string.infoTemplateIsCreated), Toast.LENGTH_LONG).show()
                  case None =>
                    Toast.makeText(context, this.getResources().getString(R.string.errTemplateCreationFailed), Toast.LENGTH_LONG).show()
                }*/
                startActivity(new Intent(this, classOf[Templates]).putExtra("groupId", templateGroupId))
                finish
            }

            def setBtnTxtColor(btnName: Button, attribName: String) {
              btnName.setTextColor(((XML.loadString(thingString) \ ("@" + attribName)).size == 0) match {
                case true => Color.RED
                case false => Color.GREEN
              })
            }
          }
        }

        def isUniqueMatterName(xmlStr: String, newMatter: String): Boolean = {
          val res0 = XML.loadString(xmlStr)
          !(res0 \\ "@matter").exists(_.text == newMatter)
        }
      }

      case _ => // ne naujas ruošinys
    }

  }


  def initTemplate(matter: String, measure: String, groupId: String): String = {
    val dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())
    <t matter={matter} g={groupId} measure={measure}>
        <stat lastuse={dateStr} updated={dateStr} created={dateStr} usecases="0"/>
    </t>.toString
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

