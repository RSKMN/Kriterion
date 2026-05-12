import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { FinancialReflection, NarrativeResponse } from '@/types/behavioral';
import { Sparkles, MessageSquare, Quote, ArrowUpRight } from 'lucide-react';

interface ReflectionCardsProps {
    insights: FinancialReflection[];
    narrative?: NarrativeResponse;
}

export const ReflectionCards: React.FC<ReflectionCardsProps> = ({ insights, narrative }) => {
    return (
        <div className="space-y-6">
            {narrative && (
                <Card className="border-none bg-gradient-to-br from-indigo-500/10 via-transparent to-transparent shadow-sm dark:bg-zinc-900/50">
                    <CardHeader className="pb-2">
                        <CardTitle className="text-sm font-semibold flex items-center gap-2 text-indigo-500 uppercase tracking-widest">
                            <Sparkles className="w-4 h-4" />
                            Behavioral Synthesis
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="relative">
                            <Quote className="absolute -top-1 -left-1 w-8 h-8 text-indigo-500/10 rotate-180" />
                            <p className="text-lg font-medium text-zinc-800 dark:text-zinc-200 leading-relaxed pl-6">
                                {narrative.narrative}
                            </p>
                        </div>
                        <div className="flex items-center justify-between text-xs text-zinc-400">
                            <div className="flex items-center gap-2">
                                <Badge variant="secondary" className="text-[10px] font-mono">Qwen2.5-32B</Badge>
                                <span>Confidence: {narrative.confidence}</span>
                            </div>
                            {narrative.isFallback && <span className="text-amber-500 font-medium">Fallback Engine Active</span>}
                        </div>
                    </CardContent>
                </Card>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {insights.map((insight, i) => (
                    <Card key={i} className="border-none shadow-sm bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm group hover:bg-zinc-50 dark:hover:bg-zinc-800/80 transition-all duration-300">
                        <CardHeader className="pb-2">
                            <div className="flex items-start justify-between">
                                <CardTitle className="text-md font-semibold text-zinc-800 dark:text-zinc-200">
                                    {insight.title}
                                </CardTitle>
                                <ArrowUpRight className="w-4 h-4 text-zinc-300 group-hover:text-indigo-500 transition-colors" />
                            </div>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <p className="text-sm text-zinc-600 dark:text-zinc-400 leading-relaxed">
                                {insight.description}
                            </p>
                            <div className="flex flex-wrap gap-2">
                                {insight.supportingSignals?.map((signal, j) => (
                                    <Badge key={j} variant="outline" className="text-[10px] bg-zinc-50 dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800 text-zinc-500">
                                        {signal}
                                    </Badge>
                                ))}
                            </div>
                            <div className="flex items-center justify-between pt-2 border-t border-zinc-100 dark:border-zinc-800 text-[10px] uppercase tracking-widest font-semibold">
                                <span className="text-zinc-400">Priority {insight.priority || '-'}</span>
                                <span className="text-indigo-500">{((insight.confidenceScore || 0) * 100).toFixed(0)}% Confidence</span>
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>
        </div>
    );
};
