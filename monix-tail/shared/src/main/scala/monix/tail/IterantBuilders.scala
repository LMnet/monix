/*
 * Copyright (c) 2014-2018 by The Monix Project Developers.
 * See the project homepage at: https://monix.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package monix.tail

import cats.Applicative
import cats.effect._
import monix.eval.{Coeval, Task}
import monix.tail.batches.{Batch, BatchCursor}
import org.reactivestreams.Publisher
import scala.collection.immutable.LinearSeq
import scala.concurrent.duration.{Duration, FiniteDuration}

/** Class defining curried `Iterant` builders, relieving the user from
  * specifying the `A` parameter explicitly.
  *
  * So instead of having to do:
  * {{{
  *   Iterant.now[Task, Int](1)
  * }}}
  *
  * You can do:
  * {{{
  *   Iterant[Task].now(1)
  * }}}
  */
class IterantBuilders[F[_]] {
  /** Aliased builder, see documentation for [[Iterant.now]]. */
  def now[A](a: A): Iterant[F,A] =
    Iterant.now(a)

  /** Aliased builder, see documentation for [[Iterant.pure]]. */
  def pure[A](a: A): Iterant[F,A] =
    Iterant.pure(a)

  /** Aliased builder, see documentation for [[Iterant.nextS]]. */
  def nextS[A](item: A, rest: F[Iterant[F, A]]): Iterant[F, A] =
    Iterant.nextS(item, rest)

  /** Aliased builder, see documentation for [[Iterant.nextCursorS]]. */
  def nextCursorS[A](cursor: BatchCursor[A], rest: F[Iterant[F, A]]): Iterant[F, A] =
    Iterant.nextCursorS(cursor, rest)

  /** Aliased builder, see documentation for [[Iterant.nextBatchS]]. */
  def nextBatchS[A](batch: Batch[A], rest: F[Iterant[F, A]]): Iterant[F, A] =
    Iterant.nextBatchS(batch, rest)

  /** Aliased builder, see documentation for [[Iterant.suspendS]]. */
  def suspendS[A](rest: F[Iterant[F, A]]): Iterant[F, A] =
    Iterant.suspendS(rest)

  /** Aliased builder, see documentation for [[Iterant.concatS]]. */
  def concatS[A](lh: F[Iterant[F, A]], rh: F[Iterant[F, A]]): Iterant[F, A] =
    Iterant.concatS(lh, rh)

  /** Aliased builder, see documentation for [[Iterant.scopeS]]. */
  def scopeS[A, B](acquire: F[A], use: A => F[Iterant[F, B]], close: (A, ExitCase[Throwable]) => F[Unit]): Iterant[F, B] =
    Iterant.scopeS(acquire, use, close)

  /** Aliased builder, see documentation for [[Iterant.lastS]]. */
  def lastS[A](item: A): Iterant[F, A] =
    Iterant.lastS(item)

  /** Aliased builder, see documentation for [[Iterant.haltS]]. */
  def haltS[A](e: Option[Throwable]): Iterant[F, A] =
    Iterant.haltS(e)

  /** Aliased builder, see documentation for [[Iterant.empty]]. */
  def empty[A]: Iterant[F, A] =
    Iterant.empty

  /** Aliased builder, see documentation for [[Iterant.raiseError]]. */
  def raiseError[A](ex: Throwable): Iterant[F, A] =
    Iterant.raiseError(ex)
}

/** Class defining curried `Iterant` builders for data types that
  * implement `cats.Applicative`.
  *
  * So instead of having to do:
  *
  * {{{
  *   Iterant.of[Task, Int](1, 2, 3)
  * }}}
  *
  * You can do:
  *
  * {{{
  *   Iterant[Task].now(1, 2, 3)
  * }}}
  */
class IterantBuildersApplicative[F[_]](implicit F: Applicative[F])
  extends IterantBuilders[F] {

  /** Given a list of elements build a stream out of it. */
  def of[A](elems: A*): Iterant[F,A] =
    Iterant.fromSeq(elems)(F)

  /** Aliased builder, see documentation for [[Iterant.liftF]]. */
  def liftF[A](a: F[A]): Iterant[F, A] =
    Iterant.liftF(a)

  /** Aliased builder, see documentation for [[Iterant.suspend[F[_],A](rest* Iterant.suspend]]. */
  def suspend[A](rest: F[Iterant[F, A]]): Iterant[F, A] =
    Iterant.suspend(rest)

  /** Aliased builder, see documentation for [[Iterant.fromArray]]. */
  def fromArray[A](xs: Array[A]): Iterant[F, A] =
    Iterant.fromArray(xs)

  /** Aliased builder, see documentation for [[Iterant.fromList]]. */
  def fromList[A](xs: LinearSeq[A]): Iterant[F, A] =
    Iterant.fromList(xs)(F)

  /** Aliased builder, see documentation for [[Iterant.fromIndexedSeq]]. */
  def fromIndexedSeq[A](xs: IndexedSeq[A]): Iterant[F, A] =
    Iterant.fromIndexedSeq(xs)(F)

  /** Aliased builder, see documentation for [[Iterant.fromSeq]]. */
  def fromSeq[A](xs: Seq[A]): Iterant[F, A] =
    Iterant.fromSeq(xs)(F)

  /** Aliased builder, see documentation for [[Iterant.fromIterable]]. */
  def fromIterable[A](xs: Iterable[A]): Iterant[F, A] =
    Iterant.fromIterable(xs)(F)

  /** Aliased builder, see documentation for [[Iterant.fromBatchCursor]]. */
  def fromBatchCursor[A](xs: BatchCursor[A]): Iterant[F, A] =
    Iterant.fromBatchCursor(xs)

  /** Aliased builder, see documentation for [[Iterant.fromBatch]]. */
  def fromBatch[A](xs: Batch[A]): Iterant[F, A] =
    Iterant.fromBatch(xs)

  /** Aliased builder, see documentation for [[Iterant.fromIterator]]. */
  def fromIterator[A](xs: Iterator[A]): Iterant[F, A] =
    Iterant.fromIterator(xs)(F)

  /** Aliased builder, see documentation for [[Iterant.range]]. */
  def range(from: Int, until: Int, step: Int = 1): Iterant[F, Int] =
    Iterant.range(from, until, step)(F)
}

/** Class defining curried `Iterant` builders for data types that
  * implement `cats.effect.Sync`.
  *
  * So instead of having to do:
  *
  * {{{
  *   Iterant.eval[Task, Int](1 + 1)
  * }}}
  *
  * You can do:
  *
  * {{{
  *   Iterant[Task].eval(1 + 1)
  * }}}
  */
class IterantBuildersSync[F[_]](implicit F: Sync[F])
  extends IterantBuildersApplicative[F] {

  /** Aliased builder, see documentation for [[Iterant.eval]]. */
  def eval[A](a: => A): Iterant[F,A] =
    Iterant.eval(a)(F)

  /** Aliased builder, see documentation for [[Iterant.eval]]. */
  def delay[A](a: => A): Iterant[F,A] =
    Iterant.delay(a)(F)

  /** Aliased builder, see documentation for [[Iterant.resource]]. */
  def resource[A](acquire: F[A])(release: A => F[Unit]): Iterant[F, A] =
    Iterant.resource(acquire)(release)

  /** Aliased builder, see documentation for [[Iterant.suspend[F[_],A](fa* Iterant.suspend]]. */
  def suspend[A](fa: => Iterant[F, A]): Iterant[F, A] =
    Iterant.suspend(fa)(F)

  /** Aliased builder, see documentation for [[Iterant.defer]]. */
  def defer[A](fa: => Iterant[F, A]): Iterant[F, A] =
    Iterant.defer(fa)(F)

  /** Aliased builder, see documentation for [[Iterant.tailRecM]]. */
  def tailRecM[A, B](a: A)(f: A => Iterant[F, Either[A, B]]): Iterant[F, B] =
    Iterant.tailRecM(a)(f)(F)

  /** Aliased builder, see documentation for [[Iterant.fromStateAction]]. */
  def fromStateAction[S, A](f: S => (A, S))(seed: => S): Iterant[F, A] =
    Iterant.fromStateAction(f)(seed)

  /** Aliased builder, see documentation for [[Iterant.fromStateActionL]]. */
  def fromStateActionL[S, A](f: S => F[(A, S)])(seed: => F[S]): Iterant[F, A] =
    Iterant.fromStateActionL(f)(seed)

  /** Aliased builder, see documentation for [[Iterant.repeat]]. */
  def repeat[A](elems: A*): Iterant[F, A] =
    Iterant.repeat(elems: _*)

  /** Aliased builder, see documentation for [[Iterant.repeatEval]]. */
  def repeatEval[A](thunk: => A): Iterant[F, A] =
    Iterant.repeatEval(thunk)

  /** Aliased builder, see documentation for [[Iterant.repeatEvalF]]. */
  def repeatEvalF[A](fa: F[A]): Iterant[F, A] =
    Iterant.repeatEvalF(fa)
}

/** Class defining curried `Iterant` builders for data types that
  * implement `cats.effect.Async`.
  *
  * So instead of having to do:
  *
  * {{{
  *   Iterant.intervalAtFixedRate[Task](1.second)
  * }}}
  *
  * You can do:
  *
  * {{{
  *   Iterant[Task].intervalAtFixedRate(1.second)
  * }}}
  *
  * @define intervalAtFixedRateDesc Creates an iterant that
  *         emits auto-incremented natural numbers (longs).
  *         at a fixed rate, as given by the specified `period`.
  *         The amount of time it takes to process an incoming
  *         value gets subtracted from provided `period`, thus
  *         created iterant tries to emit events spaced by the
  *         given time interval, regardless of how long further
  *         processing takes
  *
  * @define intervalWithFixedDelayDesc Creates an iterant that
  *         emits auto-incremented natural numbers (longs) spaced
  *         by a given time interval. Starts from 0 with no delay,
  *         after which it emits incremented numbers spaced by the
  *         `period` of time. The given `period` of time acts as a
  *         fixed delay between successive events.
  */
class IterantBuildersAsync[F[_]](implicit F: Async[F])
  extends IterantBuildersSync[F] {

  /** Aliased builder, see documentation for [[Iterant.never]]. */
  def never[A]: Iterant[F, A] =
    Iterant.suspendS(F.never)

  /**
    * Aliased builder, see documentation for
    * [[[Iterant.intervalAtFixedRate[F[_]](period* Iterant.intervalAtFixedRate]]].
    */
  def intervalAtFixedRate(period: FiniteDuration)
    (implicit timer: Timer[F]): Iterant[F, Long] =
    Iterant.intervalAtFixedRate(period)

  /**
    * Aliased builder, see documentation for
    * [[[Iterant.intervalAtFixedRate[F[_]](initialDelay* Iterant.intervalAtFixedRate]]].
    */
  def intervalAtFixedRate(initialDelay: FiniteDuration, period: FiniteDuration)
    (implicit timer: Timer[F]): Iterant[F, Long] =
    Iterant.intervalAtFixedRate(initialDelay, period)

  /**
    * Aliased builder, see documentation for
    * [[[Iterant.intervalWithFixedDelay[F[_]](delay* Iterant.intervalAtFixedRate]]].
    */
  def intervalWithFixedDelay(delay: FiniteDuration)
    (implicit timer: Timer[F]): Iterant[F, Long] =
    Iterant.intervalWithFixedDelay(delay)

  /**
    * Aliased builder, see documentation for
    * [[[Iterant.intervalWithFixedDelay[F[_]](initialDelay* Iterant.intervalAtFixedRate]]].
    */
  def intervalWithFixedDelay(initialDelay: FiniteDuration, delay: FiniteDuration)
    (implicit timer: Timer[F]): Iterant[F, Long] =
    Iterant.intervalWithFixedDelay(initialDelay, delay)


  /** Aliased builder, see documentation for [[Iterant.fromReactivePublisher]]. */
  def fromReactivePublisher[A](publisher: Publisher[A], requestCount: Int = 256): Iterant[F, A] =
    Iterant.fromReactivePublisher(publisher, requestCount)
}

object IterantBuilders {
  /** Type-class for quickly finding a suitable type and [[IterantBuilders]]
    * implementation for a given `F[_]` monadic context.
    */
  trait From[F[_]] {
    type Builders <: IterantBuilders[F]
    def instance: Builders
  }

  object From extends InstancesAsync {
    /** Implicit [[From]] instance for building [[Iterant]]
      * instances powered by [[monix.eval.Task Task]].
      */
    implicit object forTask extends From[Task] {
      type Builders = IterantBuildersAsync[Task]
      val instance = new IterantBuildersAsync[Task]
    }

    /** Implicit [[From]] instance for building [[Iterant]]
      * instances powered by [[monix.eval.Coeval Coeval]].
      */
    implicit object forCoeval extends From[Coeval] {
      type Builders = IterantBuildersSync[Coeval]
      val instance = new IterantBuildersSync[Coeval]
    }

    /** Implicit [[From]] instance for building [[Iterant]]
      * instances powered by `cats.effect.IO`.
      */
    implicit object forIO extends From[IO] {
      type Builders = IterantBuildersAsync[IO]
      val instance = new IterantBuildersAsync[IO]
    }
  }

  /** @define desc For building generic [[Iterant]] instances for
    *         data types that implement `cats.effect.Async`.
    */
  private[tail] abstract class InstancesAsync extends InstancesSync {
    /** $desc */
    implicit def forAsync[F[_]](implicit F: Async[F]): ForAsync[F] =
      new ForAsync[F]

    /** $desc */
    class ForAsync[F[_]](implicit F: Async[F]) extends From[F] {
      type Builders = IterantBuildersAsync[F]
      val instance = new IterantBuildersAsync[F]
    }
  }

  /** @define desc For building generic [[Iterant]] instances for
    *         data types that implement `cats.effect.Sync`.
    */
  private[tail] abstract class InstancesSync extends InstancesApplicative {
    /** $desc */
    implicit def forSync[F[_]](implicit F: Sync[F]): ForSync[F] =
      new ForSync[F]

    /** $desc */
    class ForSync[F[_]](implicit F: Sync[F]) extends From[F] {
      type Builders = IterantBuildersSync[F]
      val instance = new IterantBuildersSync[F]
    }
  }

  /** @define desc For building generic [[Iterant]] instances for
    *         data types that implement `cats.Applicative`.
    */
  private[tail] abstract class InstancesApplicative extends InstancesBase {
    /** $desc */
    implicit def forApplicative[F[_]](implicit F: Applicative[F]): ForApplicative[F] =
      new ForApplicative[F]

    /** $desc */
    class ForApplicative[F[_]](implicit F: Applicative[F]) extends From[F] {
      type Builders = IterantBuildersApplicative[F]
      val instance = new IterantBuildersApplicative[F]
    }
  }

  /** @define desc For building generic [[Iterant]] instances for
    *         data types with no restrictions.
    */
  private[tail] abstract class InstancesBase {
    /** $desc */
    implicit def forAny[F[_]]: ForAny[F] =
      ref.asInstanceOf[ForAny[F]]

    /** $desc */
    class ForAny[F[_]] extends From[F] {
      type Builders = IterantBuilders[F]
      val instance = new IterantBuilders[F]
    }

    // Reusable reference
    private val ref = new ForAny
  }
}
