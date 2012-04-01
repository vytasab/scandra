package lt.node.scandra.pirkimai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View._
import android.util.Log
import android.widget._
import util.{ScalaFunPro, FindView, FileUtil, OrderPurchase}
import xml.Text._
import xml._

//import lt.node.scandra.button4toast.{FindView, ScalaFunPro}

class OrderMaking extends Activity with OrderPurchase with ScalaFunPro with FindView with OnClickListener {

  private[this] var orderItemText: TextView = _
  private[this] var btnQty, btnRate, btnFatness, btnVol, btnName: Button = _
  private[this] var btnKind: Button = _
  // TODO -- private[this] var kind: SelectMany = _
  private[this] var btnNote: Button = _
  private[this] var btnOK, btnCANCEL: Button = _
  //private[this] var btnCANCEL: Button = _

  private[this] var thingNodeXmlString: String = _
  private[this] var orderNodeXmlString: String = _
  private[this] var origOrderNodeXmlString: String = _
  private[this] var attribName: String = _
  private[this] var attribValue: String = _

  /*
  <thingAttribsFixed seq="matter,measure" />
  <thingAttribsSelectOne seq="qty rate fatness vol name" />
  <thingAttribsSelectMany seq="kind" />
  <thingAttribsTextOnly seq="note" />
  <orderAttribs seq="matter,amount,rate,fatness,vol,kind,note" />
  */

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    //Log.v(Constants.TAG + " OrderMaking onCreate 00", "|" + "" + "|")
    this.setContentView(R.layout.order_making)

    this.orderItemText = findViewById(R.id.order_item_text).asInstanceOf[TextView]
    //this.orderItemText.setText(getIntent.getStringExtra("orderNodeXmlString"))

    this.btnQty = findViewById(R.id.qty_button).asInstanceOf[Button]
    this.btnQty setOnClickListener this

    this.btnRate = findViewById(R.id.rate_button).asInstanceOf[Button]
    this.btnRate setOnClickListener this

    this.btnFatness = findViewById(R.id.fatness_button).asInstanceOf[Button]
    this.btnFatness setOnClickListener this

    this.btnVol = findViewById(R.id.vol_button).asInstanceOf[Button]
    this.btnVol setOnClickListener this

    this.btnName = findViewById(R.id.name_button).asInstanceOf[Button]
    this.btnName setOnClickListener this

    this.btnKind = findViewById(R.id.kind_button).asInstanceOf[Button]
    this.btnKind setOnClickListener this

    this.btnNote = findViewById(R.id.note_button).asInstanceOf[Button]
    this.btnNote setOnClickListener this
    this.btnNote.setVisibility(VISIBLE)
    // TODO padaryti pastabos įvedimą

    this.btnOK = findViewById(R.id.OK_button).asInstanceOf[Button]
    this.btnOK setOnClickListener this

    this.btnCANCEL = findViewById(R.id.CANCEL_button).asInstanceOf[Button]
    this.btnCANCEL setOnClickListener this
    this.btnCANCEL.setVisibility(VISIBLE)

    Log.v(TAG + " OrderMaking onCreate thingNodeXmlString", "|" + getIntent.getStringExtra("thingNodeXmlString") + "|")
    Log.v(TAG + " OrderMaking onCreate orderNodeXmlString", "|" + getIntent.getStringExtra("orderNodeXmlString") + "|")
    Log.v(TAG + " OrderMaking onCreate attrib", "|" + getIntent.getStringExtra("attrib") + "|")
    Log.v(TAG + " OrderMaking onCreate value", "|" + getIntent.getStringExtra("value") + "|")

    /*getIntent.hasExtra("selectedItem") match {
      case true =>
        thingsString = getIntent.getStringExtra("thingsString")
        selectedItem = getIntent.getStringExtra("selectedItem")
      case false =>
        getIntent.hasExtra("attrib") match {
          case true =>
            attribName = getIntent.getStringExtra("attrib")
            attribValue = getIntent.getStringExtra("value")
          case false =>
            Log.e(Constants.TAG + " OrderMaking.onCreate ", <_>OrderMaking: selectedItem-false attrib-false ???</_>.text)
            Toast.makeText(getApplicationContext,
              <_>OrderMaking: selectedItem-false attrib-false ???</_>.text, Toast.LENGTH_LONG).show()
            val newActivity: Intent = new Intent(this, classOf[Main]);
            startActivity(newActivity)
        }
    }*/

    origOrderNodeXmlString = getIntent.hasExtra("origOrderNodeXmlString") match {
      case true => getIntent.getStringExtra("origOrderNodeXmlString")
      case false => ""
    }

    getIntent.hasExtra("attrib") match {
      case false =>
        getIntent.hasExtra("thingNodeXmlString") match {
          case true =>
            thingNodeXmlString = getIntent.getStringExtra("thingNodeXmlString")
            orderNodeXmlString = getIntent.getStringExtra("orderNodeXmlString")
          case false =>
            Log.e(TAG + " OrderMaking.onCreate ", <_>OrderMaking: attrib-false selectedItem-false ???</_>.text)
            Toast.makeText(getApplicationContext,
              <_>OrderMaking: attrib-false selectedItem-false ???</_>.text, Toast.LENGTH_LONG).show()
            startActivity(new Intent(this, classOf[Main]))
        }
      case true =>
        thingNodeXmlString = getIntent.getStringExtra("thingNodeXmlString")
        orderNodeXmlString = getIntent.getStringExtra("orderNodeXmlString")
        attribName = getIntent.getStringExtra("attrib")
        attribValue = getIntent.getStringExtra("value")
        // TO-done-DO kai attribValue == "_" išmesti atributą
        //orderNodeXmlString = ((XML.loadString(orderNodeXmlString)).asInstanceOf[Elem] % Map(attribName -> attribValue)).toString
        //... cause error the way above
        orderNodeXmlString = getIntent.hasExtra("attrib") match {
          case true if attribValue == "_" =>
            Log.v(TAG + " OrderMaking.onCreate removeOrderItemAttrib ", removeOrderItemAttrib(orderNodeXmlString, attribName).toString)
            removeOrderItemAttrib(orderNodeXmlString, attribName).toString
          case true if attribValue == "..." => // !!! !!! !!!
            // TO-done-DO kai attribValue == "..." padaryti teksto įvedimą
            Log.v(tagclass, "onClick btnXxxx ...")
            inputString(attribName /*null*/ , /*"Pranešimas"*/ null,
              (s: String) => {
                attribValue = (if (s == "...") "._._." else s) // to escape endless recursion
                //Log.v(TAG + " OrderMaking.onClick btnXxxx orderNodeXmlString ", orderNodeXmlString)
                Log.v(TAG + " OrderMaking.onClick btnXxxx attribName", attribName)
                Log.v(TAG + " OrderMaking.onClick btnXxxx attribValue ", attribValue)
                //-----
                Log.v(TAG + " OrderMaking.onClick btnXxxx", "onItemClick ...")
                val omActivity: Intent = new Intent(this, classOf[OrderMaking]);
                omActivity.putExtra("thingNodeXmlString", thingNodeXmlString)
                omActivity.putExtra("orderNodeXmlString", orderNodeXmlString)
                omActivity.putExtra("attrib", attribName)
                omActivity.putExtra("value", attribValue)
                getIntent.hasExtra("origOrderNodeXmlString") match {
                  case true =>
                    Log.v(TAG + " OrderMaking.onClick btnXxxx ", " origOrderNodeXmlString ...")
                    omActivity.putExtra("origOrderNodeXmlString", getIntent.getStringExtra("origOrderNodeXmlString"))
                  case _ =>
                }
                if (getIntent.hasExtra("back2PreOrder")) omActivity.putExtra("back2PreOrder", "yes")
                startActivity(omActivity)
                Log.v(TAG + " OrderMaking.onClick btnXxxx", "... onItemClick")
                //-----
              },
              () => {
                Toast.makeText(OrderMaking.this, "Jūs paspaudėte Cancel mygtuką!\nYou have clicked the button Cancel!\nScala style", Toast.LENGTH_LONG).show()
              }
            )
            Log.v(TAG + " OrderMaking.onClick btnXxxx attribValue ext ", "must be unreachable !!!")
            orderNodeXmlString
          case true =>
            ((XML.loadString(orderNodeXmlString)).asInstanceOf[Elem] % new UnprefixedAttribute(attribName, attribValue, Null)).toString
          case _ => // PreOrder use case
            orderNodeXmlString
        }

    }
    Log.v(TAG + " OrderMaking.onCreate orderNodeXmlString ", orderNodeXmlString)
    Log.v(TAG + " OrderMaking.onCreate orderItem2Str(XML.loadString(orderNodeXmlString) ", orderItem2Str(XML.loadString(orderNodeXmlString)))
    this.orderItemText.setText(orderNodeXmlString + "\n" + orderItem2Str(XML.loadString(orderNodeXmlString)))

    //val selectedItemMatter: String = selectedItem.substring(0, selectedItem.indexOf(" [")).trim()
    //val theNode: Node = getByAtt(XML.loadString(thingsString), "matter", selectedItemMatter).apply(0)
    val theNode: Node = XML.loadString(thingNodeXmlString)

    for (attrName <- List[String]("qty", "fatness", "rate", "vol", "name", "kind")) (
      //Log.v(Constants.TAG + " OrderMaking", "attrName = " + attrName);
      attrName match {
        case an if an == "qty" =>
          theNode.attributes.exists(_.key == "qty") match {
            case true =>
              this.btnQty.setVisibility(VISIBLE)
            case _ =>
              this.btnQty.setVisibility(GONE)
          }
        case an if an == "fatness" =>
          theNode.attributes.exists(_.key == "fatness") match {
            case true =>
              this.btnFatness.setVisibility(VISIBLE)
            case _ =>
              this.btnFatness.setVisibility(GONE)
          }
        case an if an == "rate" =>
          theNode.attributes.exists(_.key == "rate") match {
            case true =>
              this.btnRate.setVisibility(VISIBLE)
            case _ =>
              this.btnRate.setVisibility(GONE)
          }
        case an if an == "vol" =>
          theNode.attributes.exists(_.key == "vol") match {
            case true =>
              this.btnVol.setVisibility(VISIBLE)
            case _ =>
              this.btnVol.setVisibility(GONE)
          }
        case an if an == "name" =>
          theNode.attributes.exists(_.key == "name") match {
            case true =>
              this.btnName.setVisibility(VISIBLE)
            case _ =>
              this.btnName.setVisibility(GONE)
          }
        case an if an == "kind" =>
          theNode.attributes.exists(_.key == "kind") match {
            case true =>
              this.btnKind.setVisibility(VISIBLE)
            case _ =>
              this.btnKind.setVisibility(GONE)
          }
        case _ =>
      }
      )


    /* getStatus(getIntent.getStringExtra("orderString"), getIntent.getStringExtra("selectedItem")) match {
          case 2 =>
            this.decisionDone.setVisibility(VISIBLE)
            this.decisionPartiallyDone.setVisibility(VISIBLE)
            this.decisionRevert.setVisibility(GONE)
          case 1 =>
            this.decisionDone.setVisibility(VISIBLE)
            this.decisionPartiallyDone.setVisibility(GONE)
            this.decisionRevert.setVisibility(VISIBLE)
          case 0 =>
            this.decisionDone.setVisibility(GONE)
            this.decisionPartiallyDone.setVisibility(VISIBLE)
            this.decisionRevert.setVisibility(VISIBLE)
          case n =>
            Toast.makeText(getApplicationContext, "Netikėtas pirkimo statusas=" + n, Toast.LENGTH_LONG).show()

        }
    */

    // TO-done-DO 'qty' turi būti munerical ir nenulinis
    //    ((XML.loadString(getIntent.getStringExtra("orderNodeXmlString").toString)) \ "@qty").toString() match {
    ((XML.loadString(orderNodeXmlString)) \ "@qty").toString() match {
      case value if value.length > 0 =>
        value match {
          case x if (x.length > 0) && (x.matches("[-+]?([0-9]*\\.)?[0-9]+")) && (x.toFloat > 0) =>
            this.btnOK.setVisibility(VISIBLE)
          case _ =>
            this.btnOK.setVisibility(GONE)
        }
      case _ =>
        this.btnOK.setVisibility(GONE)
    }

  }

  def onClick(view: View) {
    view match {
      case v if (v == btnQty) || (v == btnRate) || (v == btnFatness) ||
        (v == btnVol) || (v == btnName) || (v == btnKind) /* || (v == btnNote)*/ =>
        val theNode: Node = XML.loadString(thingNodeXmlString)
        val oasIntent = new Intent(this, classOf[OrderAttribSetting])
        v match {
          case vv if (vv == btnQty) =>
            Log.v(tagclass, "onClick btnQty ...")
            oasIntent.putExtra("attrib", "qty")
            oasIntent.putExtra("values", theNode.attributes.find(_.key == "qty").get.value.text)
          case vv if (vv == btnFatness) =>
            Log.v(tagclass, "onClick btnFatness ...")
            oasIntent.putExtra("attrib", "fatness")
            oasIntent.putExtra("values", theNode.attributes.find(_.key == "fatness").get.value.text)
          case vv if (vv == btnRate) =>
            Log.v(tagclass, "onClick btnRate ...")
            oasIntent.putExtra("attrib", "rate")
            oasIntent.putExtra("values", theNode.attributes.find(_.key == "rate").get.value.text)
          case vv if (vv == btnVol) =>
            Log.v(tagclass, "onClick btnVol ...")
            oasIntent.putExtra("attrib", "vol")
            oasIntent.putExtra("values", theNode.attributes.find(_.key == "vol").get.value.text)
          case vv if (vv == btnName) =>
            Log.v(tagclass, "onClick btnName ...")
            //oasIntent.putStringArrayListExtra()
            oasIntent.putExtra("attrib", "name")
            oasIntent.putExtra("values", theNode.attributes.find(_.key == "name").get.value.text)
          case vv if (vv == btnKind) =>
            Log.v(tagclass, "onClick btnKind ...")
            oasIntent.putExtra("attrib", "kind")
            oasIntent.putExtra("values", theNode.attributes.find(_.key == "kind").get.value.text)
          case _ =>
        }
        getIntent.hasExtra("origOrderNodeXmlString") match {
          case true =>
            Log.v(tagclass, "onClick btnXxx origOrderNodeXmlString ...")
            oasIntent.putExtra("origOrderNodeXmlString", getIntent.getStringExtra("origOrderNodeXmlString"))
          case _ =>
        }
        oasIntent.putExtra("thingNodeXmlString", thingNodeXmlString)
        oasIntent.putExtra("orderNodeXmlString", orderNodeXmlString)
        if (getIntent.hasExtra("back2PreOrder")) oasIntent.putExtra("back2PreOrder", "yes")
        startActivity(oasIntent)

      case v if (v == btnNote) =>
        Log.v(tagclass, "onClick btnNote ...")
        inputString("note" /*null*/ , /*"Pranešimas"*/ null,
          (s: String) => {
            attribName = "note"
            attribValue = s
            Log.v(TAG + " OrderMaking.onClick btnNote orderNodeXmlString ", orderNodeXmlString)
            Log.v(TAG + " OrderMaking.onClick btnNote attribName ", attribName)
            Log.v(TAG + " OrderMaking.onClick btnNote attribValue ", attribValue)
            //-----
            Log.v(TAG + " OrderMaking.onClick btnNote", "onItemClick ...")
            val omActivity: Intent = new Intent(/*OrderAttribSetting.*/ this, classOf[OrderMaking]);
            omActivity.putExtra("thingNodeXmlString", thingNodeXmlString)
            omActivity.putExtra("orderNodeXmlString", orderNodeXmlString)
            omActivity.putExtra("attrib", attribName)
            omActivity.putExtra("value", s)
            getIntent.hasExtra("origOrderNodeXmlString") match {
              case true =>
                Log.v(TAG + " OrderMaking.onClick btnNote ", " origOrderNodeXmlString ...")
                omActivity.putExtra("origOrderNodeXmlString", getIntent.getStringExtra("origOrderNodeXmlString"))
              case _ =>
            }
            if (getIntent.hasExtra("back2PreOrder")) omActivity.putExtra("back2PreOrder", "yes")
            startActivity(omActivity)
            Log.v(TAG + " OrderMaking.onClick btnNote", "... onItemClick")
            //-----
          },
          () => {
            Toast.makeText(OrderMaking.this, "Jūs paspaudėte Cancel mygtuką!\nYou have clicked the button Cancel!\nScala style", Toast.LENGTH_LONG).show()
          }
        )

      case v if v == btnOK =>
        Log.v(tagclass, "onClick btnOK ...")
        val oIntent = new Intent(this, classOf[Order])
        getIntent.hasExtra("origOrderNodeXmlString") match {
          case true =>
            Log.v(tagclass, "onClick btnOK origOrderNodeXmlString ...")
            oIntent.putExtra("origOrderNodeXmlString", getIntent.getStringExtra("origOrderNodeXmlString"))
            oIntent.putExtra("back2PreOrder", "yes") // C307/vsh -- bus grįžtama į PreOrder pirkių sąrašą
          case _ =>
        }
        oIntent.putExtra("orderNodeXmlStringDone", orderNodeXmlString)
        oIntent.putExtra("back2PreOrder", "yes") // C307/vsh bus grįžtama į PreOrder pirkių sąrašą, nes jis jau netuščias
        startActivity(oIntent)

      case v if v == btnCANCEL =>
        startActivity(new Intent(this, classOf[Order]).putExtra("groupId", getIntent.getStringExtra("groupId")))
        //startActivity(new Intent(this, classOf[Groups]).putExtra("case", "Order"))

      case _ =>
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

