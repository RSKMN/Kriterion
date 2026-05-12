import React from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Database, LineChart, ShieldCheck, Zap } from 'lucide-react';

interface BehavioralEmptyStateProps {
    onSeed?: () => void;
    isSeeding?: boolean;
}

export const BehavioralEmptyState: React.FC<BehavioralEmptyStateProps> = ({ onSeed, isSeeding }) => {
    return (
        <div className="flex flex-col items-center justify-center min-h-[400px] space-y-8 animate-in fade-in duration-700">
            <div className="relative">
                <div className="absolute inset-0 bg-indigo-500/10 blur-3xl rounded-full" />
                <div className="relative bg-white dark:bg-zinc-900 p-6 rounded-2xl shadow-xl border border-zinc-100 dark:border-zinc-800">
                    <Database className="w-12 h-12 text-indigo-500" />
                </div>
            </div>

            <div className="max-w-md text-center space-y-4">
                <h2 className="text-2xl font-bold text-zinc-800 dark:text-zinc-200">
                    Awaiting Temporal Density
                </h2>
                <p className="text-zinc-500 dark:text-zinc-400 leading-relaxed">
                    Behavioral Intelligence requires transaction history to identify rhythms, patterns, and cognitive load. 
                    Your analytics will automatically appear as you record more financial activity.
                </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 w-full max-w-2xl">
                {[
                    { icon: LineChart, label: 'Trend Analysis', desc: 'Volatility & rhythm consistency' },
                    { icon: ShieldCheck, label: 'Pattern Detection', desc: 'Burst & subscription signals' },
                    { icon: Zap, label: 'Cognitive Load', desc: 'Decision pressure estimation' }
                ].map((item, i) => (
                    <Card key={i} className="border-none bg-zinc-50/50 dark:bg-zinc-900/50">
                        <CardContent className="p-4 flex flex-col items-center text-center space-y-2">
                            <item.icon className="w-5 h-5 text-zinc-400" />
                            <span className="text-xs font-bold text-zinc-700 dark:text-zinc-300">{item.label}</span>
                            <span className="text-[10px] text-zinc-500">{item.desc}</span>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {onSeed && (
                <div className="pt-4 flex flex-col items-center gap-3">
                    <span className="text-xs font-semibold text-zinc-400 uppercase tracking-widest">Researcher Tools</span>
                    <Button 
                        onClick={onSeed} 
                        disabled={isSeeding}
                        className="bg-indigo-600 hover:bg-indigo-700 text-white px-8 rounded-full shadow-lg shadow-indigo-500/20 transition-all active:scale-95"
                    >
                        {isSeeding ? 'Generating Entropy...' : 'Seed Behavioral Demo Data'}
                    </Button>
                    <p className="text-[10px] text-zinc-400">
                        Generates 60+ behaviorally meaningful transactions for evaluation.
                    </p>
                </div>
            )}
        </div>
    );
};
