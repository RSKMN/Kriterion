import React from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { CognitiveLoadResponse } from '@/types/behavioral';
import { Brain, AlertCircle, Info } from 'lucide-react';

interface CognitiveLoadCardProps {
    data: CognitiveLoadResponse;
}

export const CognitiveLoadCard: React.FC<CognitiveLoadCardProps> = ({ data }) => {
    const getImpactColor = (impact: string) => {
        switch (impact) {
            case 'HIGH': return 'text-red-500 bg-red-50 dark:bg-red-950/20';
            case 'MEDIUM': return 'text-amber-500 bg-amber-50 dark:bg-amber-950/20';
            case 'LOW': return 'text-emerald-500 bg-emerald-50 dark:bg-emerald-950/20';
            default: return 'text-gray-500 bg-gray-50 dark:bg-gray-950/20';
        }
    };

    return (
        <Card className="border-none shadow-sm bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm">
            <CardHeader className="pb-2">
                <div className="flex items-center justify-between">
                    <CardTitle className="text-lg font-semibold flex items-center gap-2">
                        <Brain className="w-5 h-5 text-indigo-500" />
                        Financial Cognitive Load
                    </CardTitle>
                    <Badge variant="outline" className="font-mono">
                        {((data.cognitiveLoadScore || 0) * 100).toFixed(0)}% Load
                    </Badge>
                </div>
                <CardDescription>
                    Estimation of financial decision pressure and behavioral complexity.
                </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
                <div className="space-y-2">
                    <div className="flex justify-between text-sm">
                        <span className="text-muted-foreground">Aggregate Pressure</span>
                        <span className="font-medium">{((data.cognitiveLoadScore || 0) * 100).toFixed(0)}/100</span>
                    </div>
                    <Progress value={(data.cognitiveLoadScore || 0) * 100} className="h-2 bg-zinc-100 dark:bg-zinc-800" />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {data.factors?.map((factor) => (
                        <div key={factor.name} className="p-3 rounded-lg border border-zinc-100 dark:border-zinc-800 bg-zinc-50/50 dark:bg-zinc-800/50">
                            <div className="flex items-center justify-between mb-1">
                                <span className="text-xs font-medium text-zinc-500 uppercase tracking-wider">{factor.name}</span>
                                <Badge variant="secondary" className={`text-[10px] px-1.5 py-0 ${getImpactColor(factor.impact)}`}>
                                    {factor.impact}
                                </Badge>
                            </div>
                            <Progress value={(factor.value || 0) * 100} className="h-1 bg-zinc-200 dark:bg-zinc-700" />
                        </div>
                    ))}
                </div>

                <div className="space-y-2">
                    <h4 className="text-xs font-semibold text-zinc-400 uppercase tracking-widest flex items-center gap-1.5">
                        <Info className="w-3 h-3" />
                        Interpretations
                    </h4>
                    <ul className="space-y-1.5">
                        {data.interpretations?.map((text, i) => (
                            <li key={i} className="text-sm flex items-start gap-2 text-zinc-600 dark:text-zinc-400">
                                <AlertCircle className="w-4 h-4 mt-0.5 text-zinc-300 dark:text-zinc-700" />
                                {text}
                            </li>
                        ))}
                    </ul>
                </div>
            </CardContent>
        </Card>
    );
};
