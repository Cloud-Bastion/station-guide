import {markRaw, ref, Ref as VueRef, unref, UnwrapRef} from "vue";
import {RefUnwrapBailTypes} from "@vue/reactivity";

export default class Ref<T = never> {
    private readonly _value : VueRef<UnwrapRefSimple<{value: T}>>;
    private callbacks: Array<(oldValue: T, newValue: T)=>void>;

    constructor(value: T) {
        this._value = ref({value}) as VueRef<UnwrapRefSimple<{value: T}>>;
        this.callbacks = [];
    }


    get value(): T {
        return (unref(this._value) as unknown as { value: T }).value;
    }

    set value(value: T) {
        const oldValue = this.value;
        ref(this._value).value.value = value as never;
        this.callbacks.forEach(callback => {
            callback.call(undefined, oldValue, this.value);
        })
    }

    get ref(): VueRef<UnwrapRefSimple<{value: T}>> {
        return this._value;
    }

    watch(callback: (oldValue: T, newValue: T) => void) {
        this.callbacks.push(callback);
    }
}
declare type WeakCollections = WeakMap<never, never> | WeakSet<never>;


declare type IterableCollections = Map<never, never> | Set<never>;

declare type CollectionTypes = IterableCollections | WeakCollections;

declare type BaseTypes = string | number | boolean;

declare const RawSymbol: unique symbol;
declare const ShallowReactiveMarker: unique symbol;

// eslint-disable-next-line @typescript-eslint/ban-types
declare type UnwrapRefSimple<T> = T extends Function | CollectionTypes | BaseTypes | VueRef | RefUnwrapBailTypes[keyof RefUnwrapBailTypes] | {
    [RawSymbol]?: true;
} ? T : T extends Array<never> ? {
    [K in keyof T]: UnwrapRefSimple<T[K]>;
} : T extends object & {
    [ShallowReactiveMarker]?: never;
} ? {
    [P in keyof T]: P extends symbol ? T[P] : UnwrapRef<T[P]>;
} : T;